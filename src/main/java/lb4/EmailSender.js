const nodemailer = require('nodemailer');
const fs = require('fs');

async function sendEmail(config, subject, toAddrs, fromAddr, bodyText) {
    const transporter = nodemailer.createTransport(config);

    const mailOptions = {
        from: fromAddr,
        to: toAddrs.join(', '),
        subject: subject,
        text: bodyText
    };

    try {
        const info = await transporter.sendMail(mailOptions);
        console.log('Email sent successfully.');
        console.log('Message ID:', info.messageId);
    } catch (error) {
        console.error('Error occurred while sending email:', error);
    }
}

const config = JSON.parse(fs.readFileSync('config.json', 'utf8'));
const subject = 'Session is soon!';
const toAddrs = ['2522va@gmail.com', "2522va@ukr.net"];
const fromAddr = '2522va@ukr.net';
const bodyText = 'Hello! \n\nWe are happy to inform you about the upcoming exam. Complete all assignments and submit all reports.\n\nRegards,\nyour nerves:)';

sendEmail(config, subject, toAddrs, fromAddr, bodyText);