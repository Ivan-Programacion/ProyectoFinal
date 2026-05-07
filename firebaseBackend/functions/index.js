const { onDocumentWritten } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * CASO 1 Y 6: EXAMEN ACTIVADO O CANCELADO
 * Escucha cambios en "exam/{centerId}"
 */
exports.notifyExamStatusChange = onDocumentWritten("exam/{centerId}", async (event) => {
    const beforeData = event.data.before ? event.data.before.data() : null;
    const afterData = event.data.after ? event.data.after.data() : null;

    if (!afterData || !beforeData) return null;

    const oldStatus = beforeData.currentStatus;
    const newStatus = afterData.currentStatus;
    const centerId = event.params.centerId;

    if (oldStatus === newStatus) return null;

    let notificationConfig = null;

    // CASO 1: ACTIVADO
    if (oldStatus !== "OPEN_REQUESTS" && newStatus === "OPEN_REQUESTS") {
        notificationConfig = {
            title: "¡Examen de Kenpo abierto!",
            body: "Se ha habilitado el proceso de examen. Entra a tu perfil para solicitarlo.",
            filterStatus: null // Para todos los alumnos activos del centro
        };
    }
    // CASO 6: CANCELADO (Pasa de cualquier estado activo a CLOSED sin haber terminado)
    else if ((oldStatus === "OPEN_REQUESTS" || oldStatus === "IN_PROGRESS") && newStatus === "CLOSED") {
        notificationConfig = {
            title: "Examen cancelado",
            body: "El proceso de examen ha sido cancelado. Lo sentimos.",
            filterStatus: "NOT_NONE" // Para todos los que estaban participando
        };
    }

    if (notificationConfig) {
        let query = admin.firestore().collection("users")
            .where("centerId", "==", centerId)
            .where("active", "==", true)
            .where("clientApproved", "==", true);

        const usersSnapshot = await query.get();
        const tokens = [];

        usersSnapshot.forEach(doc => {
            const user = doc.data();
            // Si es cancelación, filtramos que su examStatus no sea NONE
            if (notificationConfig.filterStatus === "NOT_NONE") {
                if (user.examStatus !== "NONE" && user.fcmToken) tokens.push(user.fcmToken);
            } else {
                if (user.fcmToken) tokens.push(user.fcmToken);
            }
        });

        if (tokens.length > 0) {
            const message = {
                notification: { title: notificationConfig.title, body: notificationConfig.body },
                tokens: tokens
            };
            await admin.messaging().sendEachForMulticast(message);
        }
    }
    return null;
});

/**
 * CASOS 2, 3, 4 y 5: CAMBIOS INDIVIDUALES (users/{userId})
 */
exports.notifyStudentExamStatus = onDocumentWritten("users/{userId}", async (event) => {
    const beforeData = event.data.before ? event.data.before.data() : null;
    const afterData = event.data.after ? event.data.after.data() : null;

    if (!afterData || !beforeData) return null;

    const oldStatus = beforeData.examStatus;
    const newStatus = afterData.examStatus;
    const fcmToken = afterData.fcmToken;

    if (oldStatus === newStatus || !fcmToken) return null;

    let title = "";
    let body = "";

    switch (newStatus) {
        case "CANDIDATE": // SOLICITUD APROBADA
            title = "¡Solicitud aprobada!";
            body = "Se ha aprobado tu solicitud. ¡El examen está a punto de empezar!";
            break;
        case "REFUSED": // SOLICITUD RECHAZADA
            title = "Solicitud rechazada";
            body = "Tu solicitud de examen no ha sido aprobada en esta ocasión.";
            break;
        case "APPROVED": // EXAMEN APROBADO (CASO ESPECIAL)
            // Verificamos si el examen ya terminó
            const examDoc = await admin.firestore().collection("exam").doc(afterData.centerId).get();
            if (examDoc.exists && examDoc.data().currentStatus === "IN_PROGRESS") {
                console.log("Alumno aprobado individualmente pero examen sigue IN_PROGRESS. No notificamos aún.");
                return null;
            }
            title = "¡Enhorabuena, has aprobado!";
            body = "Has superado el examen. ¡Revisa tu nuevo cinturón en la App!";
            break;
        case "FAILED": // EXAMEN SUSPENSO
            title = "Examen no superado";
            body = "No has superado el examen esta vez. ¡Sigue entrenando duro!";
            break;
        default:
            return null;
    }

    const message = {
        notification: { title, body },
        token: fcmToken
    };

    try {
        await admin.messaging().send(message);
    } catch (error) {
        if (error.code === 'messaging/registration-token-not-registered') {
            await admin.firestore().collection("users").doc(event.params.userId).update({ fcmToken: admin.firestore.FieldValue.delete() });
        }
    }
    return null;
});