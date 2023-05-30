const nodemailer = require('nodemailer');
const fs = require('fs');

async function sendEmail() {
    const config = JSON.parse(fs.readFileSync('config.json', 'utf8'));
    const transporter = nodemailer.createTransport(config)

    const message = {
        from: config.auth.user,
        to: '2522va@gmail.com',
        subject: 'Test alternative TEXT and HTML',
        text: `Привет!\nКак у вас дела?`,
        html: `<body><main><p>Привет</p><br>Как у вас <strong>дела?</strong></main></body>`
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
