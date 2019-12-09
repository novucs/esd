function verifyPayment(paymentId) {
  document.getElementById('verifyPayment_' + paymentId.toString()).submit();
}
function declinePayment(paymentId) {
  document.getElementById('declinePayment_' + paymentId.toString()).submit();
}
function pendingPayment(paymentId) {
  document.getElementById('pendingPayment_' + paymentId.toString()).submit();
}
