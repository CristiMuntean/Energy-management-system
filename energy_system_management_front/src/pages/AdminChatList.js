import React, {useEffect, useState} from "react";
import {IoChatbubblesOutline, IoClose} from "react-icons/io5";
import Chat from "./Chat";
import SockJsClient from "react-stomp";

export default function AdminChatList({activeChats}) {
    const MESSAGE_SOCKET_URL = 'http://localhost:8084/ws-message'
    const [isChatListShown, setIsChatListShown] = useState(false);
    const [openChats, setOpenChats] = useState([]);
    const [clientRef, setClientRef] = useState(null);
    const [newMessage, setNewMessage] = useState(null);
    const [newMessagesStates, setNewMessagesStates] = useState(
        activeChats.map((chat) => chat.username).map((username) => ({username, newMessage: false}))
    );
    const [chatList, setChatList] = useState([]);

    const toggleChatList = () => {
        setIsChatListShown(!isChatListShown);
        setOpenChats([]);
    };

    useEffect(() => {
        const tempList = JSON.parse(localStorage.getItem("activeChatList")) || [];
        setChatList(tempList);
        console.log("chatList: ", tempList);
    }, [isChatListShown]);

    useEffect(() => {
        setClientRef(clientRef);
    }, [clientRef]);

    useEffect(() => {
        console.log("activeChats inside chatlist: ", activeChats);
        const newMessagesStatesCopy = [...newMessagesStates];
        const newUsername = activeChats[activeChats.length - 1].username;
        newMessagesStatesCopy.push({username: newUsername, newMessage: false});
        setNewMessagesStates(newMessagesStatesCopy);

        console.log("newMessagesStates inside chatlist: ", newMessagesStates);
    }, [activeChats]);

    const onConnected = () => {
        console.log('Admin chat connected!!!');
    }

    const onMessageReceived = (msg) => {
        console.log('Message received: ', msg);
        setNewMessage(msg);
        if (msg.type === "message") {
            console.log("NEW MESSAGE");

            if (!openChats.includes(msg.sender)) {
                const newMessagesStatesCopy = [...newMessagesStates];
                const index = newMessagesStatesCopy.findIndex((state) => state.username === msg.sender);
                newMessagesStatesCopy[index].newMessage = true;
                setNewMessagesStates(newMessagesStatesCopy);
                const allMessages = JSON.parse(localStorage.getItem("messages")) || [];
                const updatedMessages = [...allMessages, msg];
                localStorage.setItem("messages", JSON.stringify(updatedMessages));
                setNewMessage(null);
            }
        }
    }

    const onDisconnected = () => {
        console.log('Disconnected!!!');
    }

    const openChat = (username) => {
        const updatedChats = [...openChats];
        if (!updatedChats.includes(username)) {
            if (updatedChats.length === 3) {
                updatedChats.pop();
            }
            updatedChats.unshift(username);
            setOpenChats(updatedChats);
            const newMessagesStatesCopy = [...newMessagesStates];
            const index = newMessagesStatesCopy.findIndex((state) => state.username === username);
            newMessagesStatesCopy[index].newMessage = false;
            setNewMessagesStates(newMessagesStatesCopy);
        }
    }

    const closeChat = (username) => {
        console.log(username + " is closed");
        setOpenChats(openChats.filter((chat) => chat !== username));
    }

    return (
        <div className="flex flex-row justify-end">
            <SockJsClient
                url={MESSAGE_SOCKET_URL}
                topics={['/topic/message/admin']}
                onConnect={onConnected}
                onDisconnect={onDisconnected}
                onMessage={msg => onMessageReceived(msg)}
                debug={false}
                ref={(client) => {
                    setClientRef(client)
                }}
            />

            {!isChatListShown && (
                <div
                    className="bg-gray-100 p-4 rounded-md shadow-md w-1/4 flex justify-between items-center hover:cursor-pointer hover:bg-gray-200"
                    onClick={toggleChatList}
                >
                    <h1 className="text-2xl">Active Chats</h1>
                    <IoChatbubblesOutline className="text-3xl ml-2" height="25" width="25"/>
                </div>
            )}

            {isChatListShown &&
                openChats.map((username) => (
                    <Chat key={username} username={username} toggleChat={() => closeChat(username)}
                          clientRef={clientRef} newMessage={newMessage} setNewMessage={setNewMessage}/>
                ))
            }

            {isChatListShown && (
                <div className="bg-gray-50 p-4 rounded-md shadow-md flex flex-col w-1/4 h-96">
                    <div className="flex justify-between mb-2">
                        <div className="">
                            <h2 className="font-bold text-lg">Active chats</h2>
                        </div>
                        <div className="flex items-center">
                            <IoClose
                                className="hover:cursor-pointer text-2xl"
                                height="25"
                                width="25"
                                onClick={toggleChatList}
                            />
                        </div>
                    </div>
                    <ul className="overflow-y-auto">
                        {chatList.map((chat, index) => (
                            <li
                                key={index}
                                className="cursor-pointer hover:bg-gray-200 p-2 rounded-md mb-1 items-center flex justify-between"
                                onClick={() => openChat(chat.username)}
                            >
                                <p>{chat.username}</p>
                                {newMessagesStates.find((state) => state.username === chat.username).newMessage && (
                                    <div className="flex justify-end">
                                        <span className="text-xs text-gray-500">new</span>
                                    </div>
                                )}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

