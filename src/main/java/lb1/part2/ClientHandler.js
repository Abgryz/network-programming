process.on('message', (m, socket) => {
    if (m === 'socket') {
        if (socket) {
            handleClient(socket);
        }
    }
});

function handleClient(socket) {
    console.log('Client connected:', socket.remotePort);

    socket.on('data', (data) => {
        console.log('Received message from client:', data.toString());
        socket.write('Server: ' + data.toString());
    });
    socket.on('close', () => {
        console.log('Connection with', socket.remotePort, 'closed')
    });
    socket.on('error', () => {
        console.log('Connection with', socket.remotePort, 'failed')
    })

}
