const ftp = require('basic-ftp');
const fs = require('fs');

const server = 'ftp.ubuntu.com';
const port = 21;
const username = 'anonymous';
const password = '';

const remoteDirectory = '/ubuntu/dists/';
const localDirectory = '/Projects/NP/src/main/resources/output';
async function findAndDownloadManifest(client, remoteDirectory, localDirectory, iteration) {
    // Переход в выбранную директорию
    await client.cd(remoteDirectory);

    // Получение списка файлов в текущей директории
    const fileNames = await client.list();

    for (const fileName of fileNames) {
        // Поиск файла MANIFEST
        if (fileName.name.toLowerCase() === 'manifest') {
            console.log('MANIFEST founded');
            // Загрузка файла MANIFEST в соответствующую директорию
            const localFilePath = `${localDirectory}${remoteDirectory}main/${fileName.name}`;
            await fileDownloader(client, localFilePath, fileName.name);
            console.log(`MANIFEST downloaded ${localFilePath}`);
            return;
        }
    }

    // Анализ вложенных директорий
    const subDirectories = await client.list();
    for (const subDirectory of subDirectories) {
        if (
            (await client.cd(`${remoteDirectory}/${subDirectory.name}`)) &&
            (subDirectory.name.startsWith('installer') || iteration !== 1)
        ) {
            console.log(`finding in ${remoteDirectory}/${subDirectory.name}`);
            await findAndDownloadManifest(client, `${remoteDirectory}/${subDirectory.name}`, localDirectory, iteration + 1);
            await client.cd('..');
        }
    }
}

async function fileDownloader(client, localFilePath, fileName) {
    const localFileDir = localFilePath.substring(0, localFilePath.lastIndexOf('/'));
    fs.mkdirSync(localFileDir, { recursive: true }); // Создание необходимых директорий, если их еще нет

    const outputStream = fs.createWriteStream(localFilePath);
    await client.download(outputStream, fileName);
}

async function start() {
    const client = new ftp.Client();
    try {
        await client.access({
            host: server,
            port: port,
            user: username,
            password: password
        });

        // Переход в каталог /ubuntu/dists/
        await client.cd(remoteDirectory);

        // Получение списка файлов
        const fileNames = await client.list();

        // Запись списка файлов в локальный файл
        const writer = fs.createWriteStream(`${localDirectory}/ubuntu_dists.txt`);
        for (const fileName of fileNames) {
            writer.write(fileName.name);
            writer.write('\n');
            if (fileName.name.endsWith('updates')) {
                const findingDir = `${remoteDirectory}${fileName.name}/main`;
                console.log(`Finding manifest in ${findingDir}`);
                await findAndDownloadManifest(client, findingDir, localDirectory, 1);
            }
        }
        writer.end();
        console.log('ubuntu_dists.txt saved');

        console.log('Operation completed successfully');
    } catch (error) {
        console.error(error);
        console.log('Connection with FTP server failed.');
    } finally {
        client.close();
    }
}

start();
