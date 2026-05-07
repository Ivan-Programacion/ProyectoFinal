const { onDocumentUpdated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * CASO 1: EXAMEN ACTIVADO
 * Escucha cambios en la colección "exam". Si currentStatus cambia a "OPEN_REQUESTS",
 * busca a todos los alumnos de ese centerId con fcmToken válido y les manda una notificación.
 */
exports.notifyExamActivated = onDocumentUpdated("exam/{examId}", async (event) => {
    const beforeData = event.data.before.data();
    const afterData = event.data.after.data();

    // Verificamos que el examen ha cambiado de estado y ahora es "OPEN_REQUESTS"
    if (beforeData.currentStatus !== "OPEN_REQUESTS" && afterData.currentStatus === "OPEN_REQUESTS") {
        const centerId = event.params.examId; // El ID del doc coincide con el centerId
        console.log(`Examen abierto para el centro: ${centerId}`);

        // Buscamos a los alumnos activos del centro que tengan fcmToken
        const usersSnapshot = await admin.firestore().collection("users")
            .where("centerId", "==", centerId)
            .where("isActive", "==", true)
            .where("clientApproved", "==", true)
            .get();

        if (usersSnapshot.empty) {
            console.log("No se encontraron alumnos para notificar.");
            return null;
        }

        const tokens = [];
        usersSnapshot.forEach(doc => {
            const user = doc.data();
            if (user.fcmToken) {
                tokens.push(user.fcmToken);
            }
        });

        if (tokens.length === 0) {
            console.log("Los alumnos no tienen tokens FCM guardados.");
            return null;
        }

        // Preparamos el mensaje Multicast
        const message = {
            notification: {
                title: "¡Examen de Kenpo abierto!",
                body: "Se ha habilitado el proceso de examen. Entra a tu perfil para solicitarlo."
            },
            tokens: tokens
        };

        try {
            const response = await admin.messaging().sendEachForMulticast(message);
            console.log(`Notificaciones enviadas. Éxito: ${response.successCount}, Fallo: ${response.failureCount}`);
        } catch (error) {
            console.error("Error enviando notificaciones multicast:", error);
        }
    }
    return null;
});

/**
 * CASOS 2, 3, 4 y 5: CAMBIOS DE ESTADO INDIVIDUAL DEL ALUMNO
 * Escucha cambios en la colección "users" y evalúa "examStatus".
 */
exports.notifyStudentExamStatus = onDocumentUpdated("users/{userId}", async (event) => {
    const beforeData = event.data.before.data();
    const afterData = event.data.after.data();

    const oldStatus = beforeData.examStatus;
    const newStatus = afterData.examStatus;

    // Si el estado del examen no ha cambiado, no hacemos nada
    if (oldStatus === newStatus) return null;

    const fcmToken = afterData.fcmToken;
    if (!fcmToken) {
        console.log(`El usuario ${event.params.userId} no tiene fcmToken. No se puede notificar.`);
        return null;
    }

    let title = "";
    let body = "";

    // Evaluamos el nuevo estado para personalizar el mensaje
    switch (newStatus) {
        case "CANDIDATE": // SOLICITUD APROBADA
            title = "¡Solicitud de examen aprobada!";
            body = "Se ha aprobado tu solicitud. ¡Prepárate bien para el examen!.";
            break;
        case "REFUSED": // SOLICITUD RECHAZADA
            title = "Solicitud rechazada";
            body = "Lo sentimos, tu solicitud de examen no ha sido aprobada en esta ocasión.";
            break;
        case "APPROVED": // EXAMEN APROBADO
            title = "¡Enhorabuena, has aprobado!";
            body = "Has superado el examen con éxito. ¡Revisa tu nuevo nivel de cinturón en la App!";
            break;
        case "FAILED": // EXAMEN SUSPENSO
            title = "Examen no superado";
            body = "No has superado el examen esta vez. ¡Sigue entrenando duro para el próximo!";
            break;
        default:
            return null; // Si pasa a NONE, APPLICANT, etc, no enviamos notificación.
    }

    const message = {
        notification: {
            title: title,
            body: body
        },
        token: fcmToken
    };

    try {
        const response = await admin.messaging().send(message);
        console.log(`Notificación enviada a usuario ${event.params.userId}: ${response}`);
    } catch (error) {
        console.error("Error enviando notificación al usuario:", error);

        // Limpiar token si el error es "UNREGISTERED" (usuario desinstaló la app)
        if (error.code === 'messaging/registration-token-not-registered') {
             console.log(`Borrando token obsoleto del usuario ${event.params.userId}`);
             await admin.firestore().collection("users").doc(event.params.userId).update({
                 fcmToken: admin.firestore.FieldValue.delete()
             });
        }
    }

    return null;
});
