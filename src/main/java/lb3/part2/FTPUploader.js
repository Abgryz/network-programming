const ftp = require('basic-ftp');
const fs = require('fs');

const FILE_PATH_1 = '/Projects/NP/src/main/resources/input/anotherfile.txt';
const FILE_PATH_2 = '/Projects/NP/src/main/resources/input/Styx.mp4';
const server = 'localhost';
const port = 21;
const username = 'ftp';
const password = 'ftp';

async function ftpUpload(ftpClient, filePath, fileType) {
    const file = new fs.ReadStream(filePath);
    const fileName = file.path.split('/').pop();

    if (fileType.toUpperCase() === 'TXT') {
        await ftpClient.send('TYPE', 'A');
        await ftpClient.upload(file, fileName);
    } else {
        await ftpClient.send('TYPE', 'I');
        await ftpClient.upload(file, fileName);
    }

    file.destroy();
}

async function main() {
    const client = new ftp.Client();
    try {
        await client.access({
            host: server,
            port: port,
            user: username,
            password: password
        });

        await ftpUpload(client, FILE_PATH_1, 'TXT');
        await ftpUpload(client, FILE_PATH_2, 'PDF');

        console.log('Files uploaded successfully');
    } catch (error) {
        console.error(error);
        console.log('FTP upload failed.');
    } finally {
        client.close();
    }
}

main();
