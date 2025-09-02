const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendLoginNotification = functions.auth.user().onCreate((user) => {
  const fcmToken = user.customClaims?.fcmToken;

  if (!fcmToken) {
    console.log("No FCM token available.");
    return null;
  }

  const payload = {
    notification: {
      title: "Welcome back!",
      body: `Hello ${user.displayName || "User"}! You have successfully logged in.`,
    },
    token: fcmToken,
  };

  return admin.messaging().send(payload)
    .then(() => console.log("Login notification sent"))
    .catch((error) =>
      console.error("Error sending login notification:", error)
    );
});
