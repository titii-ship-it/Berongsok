const nodemailer = require('nodemailer');
const fs = require('fs');
const path = require('path');
const handlebars = require('handlebars');

function renderTemplate(templateName, data) {
  try {
    const filePath = path.join(__dirname, `mailTemplate` ,`${templateName}.html`);
    const source = fs.readFileSync(filePath, 'utf-8').toString();
    const template = handlebars.compile(source);
    const output = template(data);
    return output;
  } catch (error) {
    console.error('Error rendering email template:', error);
    throw new Error('Terjadi kesalahan pada server saat mengirim email.');
  }
}

async function sendEmail(userEmail, subject, templateName, data) {
  // Buat transporter menggunakan konfigurasi SMTP
  const transporter = nodemailer.createTransport({
    service: 'Gmail',
    auth: {
      user: process.env.EMAIL_USERNAME,
      pass: process.env.EMAIL_PASSWORD,
    },
  });

  // ambil html
  const htmlContent = renderTemplate(templateName, data);

  const mailOptions = {
    from: `"Berongsok App" <${process.env.EMAIL_USERNAME}>`,
    to: userEmail,
    subject,
    html: htmlContent,
  };


  try {
    await transporter.sendMail(mailOptions);
  } catch (error) {
    console.error('Error sending email:', error);
    throw new Error('Terjadi kesalahan pada server saat mengirim email.');
  }
}

module.exports = {
  sendEmail,
};