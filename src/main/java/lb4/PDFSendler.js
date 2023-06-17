const nodemailer = require('nodemailer');
const fs = require('fs');

async function sendEmail() {
    const config = JSON.parse(fs.readFileSync('config.json', 'utf8'));
    const transporter = nodemailer.createTransport(config);

    const message = {
        from: config.auth.user,
        to: ['v.anufriev2003@gmail.com', '2522va@gmail.com', '2522va@ukr.net'],
        cc: ['v.anufriev2003@gmail.com', '2522va@gmail.com', '2522va@ukr.net'],
        subject: 'Test send PDF',
        text: 'I send you this source of pure pleasure',
        attachments: [
            {
                filename: 'README.pdf',
                path: '../../resources/input/pdf.pdf',
                contentType: 'application/pdf'
            }
        ]
    };

    try {
        const info = await transporter.sendMail(message);
        console.log('Email sent successfully.');
        console.log('Message ID:', info.messageId);
    } catch (error) {
        console.error('Error occurred while sending email:', error);
    }
}

sendEmail();
