const { onDocumentWritten } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * CASO 1, 4, 5 y 6: GESTIÓN DEL CIERRE O APERTURA DEL EXAMEN
 * Detecta si el examen se abre, se termina o se cancela.
 */
exports.notifyExamStatusChange = onDocumentWritten("exam/{centerId}", async (event) => {
    const beforeData = event.data.before ? event.data.before.data() : null;
    const afterData = event.data.after ? event.data.after.data() : null;

    if (!afterData || !beforeData) return null;

    const oldStatus = beforeData.currentStatus;
    const newStatus = afterData.currentStatus;
    const infoMessage = afterData.infoMessage;
    const centerId = event.params.centerId;

    if (oldStatus === newStatus) return null;

    // --- CASO 1: EXAMEN ACTIVADO (Abierto para solicitudes) ---
    if (oldStatus !== "OPEN_REQUESTS" && newStatus === "OPEN_REQUESTS") {
        const usersSnapshot = await admin.firestore().collection("users")
            .where("centerId", "==", centerId)
            .where("active", "==", true)
            .where("clientApproved", "==", true)
            .get();

        const tokens = [];
        usersSnapshot.forEach(doc => {
            const user = doc.data();
            if (user.fcmTokens && user.fcmTokens.length > 0) tokens.push(...user.fcmTokens);
        });

        if (tokens.length > 0) {
            await admin.messaging().sendEachForMulticast({
                notification: {
                    title: "¡Examen de Kenpo abierto!",
                    body: "Se ha habilitado el proceso de examen. Entra a tu perfil para solicitarlo."
                },
                tokens: tokens
            });
        }
    }

    // --- CASOS 4, 5 y 6: EXAMEN CERRADO (Terminado o Cancelado) ---
    else if (newStatus === "CLOSED") {
        const usersSnapshot = await admin.firestore().collection("users")
            .where("centerId", "==", centerId)
            .where("active", "==", true)
            .where("clientApproved", "==", true)
            .get();

        const approvedTokens = [];
        const cancelledTokens = [];

        const isCancellation = (infoMessage === "CANCELLED");

        usersSnapshot.forEach(doc => {
            const user = doc.data();
            if (!user.fcmTokens || user.fcmTokens.length === 0) return;

            if (isCancellation) {
            // Si el admin canceló, todos los que estaban en el proceso reciben "Cancelado"
                if (user.examStatus !== "NONE") {
                    cancelledTokens.push(...user.fcmTokens);
                }
            } else {
            // Si NO es cancelación (Cierre normal), seguimos la lógica anterior
                if (user.examStatus === "APPROVED") {
                    approvedTokens.push(...user.fcmTokens);
                } else if (user.examStatus === "CANDIDATE" || user.examStatus === "APPLICANT") {
                    cancelledTokens.push(...user.fcmTokens);
                }
            }
        });

        // Enviar felicitaciones a los aprobados
        if (approvedTokens.length > 0) {
            await admin.messaging().sendEachForMulticast({
                notification: {
                    title: "¡Enhorabuena, has aprobado!",
                    body: "Has superado el examen con éxito. ¡Revisa tu nuevo cinturón en la App!"
                },
                tokens: approvedTokens
            });
        }

        // Enviar aviso de cancelación a los que se quedaron a medias
        if (cancelledTokens.length > 0) {
            await admin.messaging().sendEachForMulticast({
                notification: {
                    title: "Examen cancelado",
                    body: "El proceso de examen ha sido cancelado por el administrador."
                },
                tokens: cancelledTokens
            });
        }
    }

    return null;
});

/**
 * CASOS 2, 3: CAMBIOS INDIVIDUALES (users/{userId})
 * Notifica inmediatamente en cambios de estado
 */
exports.notifyStudentExamStatus = onDocumentWritten("users/{userId}", async (event) => {
    const beforeData = event.data.before ? event.data.before.data() : null;
    const afterData = event.data.after ? event.data.after.data() : null;

    if (!afterData || !beforeData) return null;

    const oldStatus = beforeData.examStatus;
    const newStatus = afterData.examStatus;
    const fcmTokens = afterData.fcmTokens;

    if (oldStatus === newStatus || !fcmTokens || fcmTokens.length === 0) return null;

    let title = "";
    let body = "";

    switch (newStatus) {
        case "CANDIDATE": // SOLICITUD APROBADA (Individual)
            title = "¡Solicitud aprobada!";
            body = "Se ha aprobado tu solicitud. ¡El examen está a punto de empezar!";
            break;
        case "REFUSED": // SOLICITUD RECHAZADA (Individual)
            title = "Solicitud rechazada";
            body = "Tu solicitud de examen no ha sido aprobada en esta ocasión.";
            break;
        case "FAILED": // EXAMEN SUSPENSO (Individual)
            title = "Examen no superado";
            body = "No has superado el examen esta vez. ¡Sigue entrenando duro!";
            break;
        case "APPROVED":
            // Para el caso APPROVED, no hacemos nada aquí.
            // Esperamos a que el examen pase a CLOSED para que lo gestione la función de arriba.
            return null;
        default:
            return null;
    }

    try {
        const response = await admin.messaging().sendEachForMulticast({
            notification: { title, body },
            tokens: fcmTokens
        });

        const invalidTokens = [];
        response.responses.forEach((res, idx) => {
            if (!res.success && res.error && res.error.code === 'messaging/registration-token-not-registered') {
                invalidTokens.push(fcmTokens[idx]);
            }
        });

        if (invalidTokens.length > 0) {
            await admin.firestore().collection("users").doc(event.params.userId).update({
                fcmTokens: admin.firestore.FieldValue.arrayRemove(...invalidTokens)
            });
        }
    } catch (error) {
        console.error("Error sending message:", error);
    }
    return null;
});