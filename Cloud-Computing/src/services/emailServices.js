const nodemailer = require('nodemailer');
const fs = require('fs');
const path = require('path');
const handlebars = require('handlebars');
const errorService = require('./error');

function renderTemplate(templateName, data) {
  try {
    const filePath = path.join(__dirname, `mailTemplate` ,`${templateName}.html`);
    const source = fs.readFileSync(filePath, 'utf-8').toString();
    const template = handlebars.compile(source);
    const output = template(data);
    return output;
  } catch (error) {
    console.error('Error rendering email template:', error);
    throw new errorService.InternalServerError('An error occurred on the server while sending the email. Please try again later.');
  }
}

async function sendEmail(userEmail, subject, templateName, data) {
  try {
    // Create transporter using SMTP configuration
    const transporter = nodemailer.createTransport({
      service: 'Gmail',
      auth: {
        user: process.env.EMAIL_USERNAME,
        pass: process.env.EMAIL_PASSWORD,
      },
    });
  
    // get html content
    const htmlContent = renderTemplate(templateName, data);
  
    const mailOptions = {
      from: `"Berongsok App" <${process.env.EMAIL_USERNAME}>`,
      to: userEmail,
      subject,
      html: htmlContent,
    };

    await transporter.sendMail(mailOptions);
  } catch (error) {
    console.error('Error sending email:', error);
    throw new errorService.InternalServerError('An error occurred on the server while sending the email. Please try again later.');
  }
}

module.exports = {
  sendEmail,
};