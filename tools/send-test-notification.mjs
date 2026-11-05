/**
 * Sends one test notification to a registered Firebase Installation ID (FID).
 * Set GOOGLE_APPLICATION_CREDENTIALS and HATEF_DEVICE_FID. Never commit credentials.
 */
import { applicationDefault, initializeApp } from 'firebase-admin/app';
import { getMessaging } from 'firebase-admin/messaging';

const fid = process.env.HATEF_DEVICE_FID;
if (!fid) {
  console.error('Missing HATEF_DEVICE_FID. Copy it from the debug APK.');
  process.exit(1);
}
if (!process.env.GOOGLE_APPLICATION_CREDENTIALS) {
  console.error('Missing GOOGLE_APPLICATION_CREDENTIALS path to a service-account JSON.');
  process.exit(1);
}

initializeApp({
  credential: applicationDefault(),
  projectId: 'hatef-platform',
});

const id = await getMessaging().send({
  fid,
  notification: {
    title: 'Hatef',
    body: 'FCM is working on this Android installation.',
  },
  data: {
    path: '/notifications',
  },
  android: {
    priority: 'normal',
  },
});
console.log('Accepted by FCM (not a delivery receipt):', id);
