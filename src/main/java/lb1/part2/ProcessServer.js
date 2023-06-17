
const net = require('net');
const { fork } = require('child_process');

const server = net.createServer((socket) => {
    const childProcess = fork('clientHandler.js');
    childProcess.send('socket', socket);
});

server.listen(7777, () => {
    console.log('Server is running on port 7777');
});
