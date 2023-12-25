import {useEffect, useState} from "react";
import {IoChatbubblesOutline} from "react-icons/io5";
import Chat from "./Chat";
import SockJsClient from "react-stomp";

export default function SupportChat() {
    const MESSAGE_SOCKET_URL = 'http://localhost:8084/ws-message'
    const user = JSON.parse(localStorage.getItem("user"));
    const [isChatShown, setIsChatShown] = useState(false);
    const [clientRef, setClientRef] = useState(null);
    const [newMessage, setNewMessage] = useState(null);

    const toggleChat = () => {
        setIsChatShown(!isChatShown);
    }

    useEffect(() => {
        setClientRef(clientRef);
    }, [clientRef]);

    const onConnected = () => {
        console.log('Support chat connected!!!');
        setTimeout(() => {
        }, 1000);
    }

    const onMessageReceived = (msg) => {
        console.log('Message received: ', msg);
        setNewMessage(msg);
    }

    const onDisconnected = () => {
        console.log('Disconnected!!!');
    }

    return <>
        <SockJsClient
            url={MESSAGE_SOCKET_URL}
            topics={['/topic/message/' + user.username]}
            onConnect={onConnected}
            onDisconnect={onDisconnected}
            onMessage={msg => onMessageReceived(msg)}
            debug={false}
            ref={(client) => {
                setClientRef(client)
            }}
        />
        {
            (!isChatShown && (
                <div
                    className="bg-green-500 p-4 rounded-md shadow-md flex justify-between items-center hover:cursor-pointer hover:bg-green-600"
                    onClick={toggleChat}>
                    <h1 className="text-2xl text-white">Contact Support</h1>
                    <IoChatbubblesOutline className="text-white text-3xl ml-2" height="25" width="25"/>
                </div>
            ))
            || (isChatShown &&
                <Chat username="admin" toggleChat={toggleChat} clientRef={clientRef} newMessage={newMessage}
                      setNewMessage={setNewMessage}/>)
        }
    </>
}