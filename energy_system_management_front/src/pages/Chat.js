import {useEffect, useRef, useState} from "react";
import {IoClose} from "react-icons/io5";

export default function Chat({username, toggleChat, clientRef, newMessage, setNewMessage}) {
    const currentUsername = JSON.parse(localStorage.getItem("user")).username;

    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const messagesContainerRef = useRef(null);
    const [typing, setTyping] = useState(false);
    const [typingSend, setTypingSend] = useState(false);

    const [isFocused, setIsFocused] = useState(false);
    const [isLastMessageSeen, setIsLastMessageSeen] = useState(false);


    useEffect(() => {
        const tempMessages = JSON.parse(localStorage.getItem("messages"));

        if (tempMessages) {
            const filteredMessages = tempMessages.filter(
                (msg) =>
                    (msg.receiver === username || msg.receiver === currentUsername) &&
                    (msg.sender === username || msg.sender === currentUsername)
            );
            setMessages(filteredMessages);
        } else setMessages([]);
    }, [username, currentUsername]);

    const handleSendMessage = () => {
        if (message.trim() !== '') {
            if (localStorage.getItem("messages") == null) localStorage.setItem("messages", JSON.stringify([]));
            const allMessages = JSON.parse(localStorage.getItem("messages")) || [];

            const newMessageObj = {text: message, sender: currentUsername, receiver: username};
            const updatedMessages = [...allMessages, newMessageObj];
            localStorage.setItem("messages", JSON.stringify(updatedMessages));
            const filteredMessages = updatedMessages.filter(
                (msg) =>
                    (msg.receiver === username || msg.receiver === currentUsername) &&
                    (msg.sender === username || msg.sender === currentUsername)
            );
            setMessages(filteredMessages);

            const messageToSend = {text: message, sender: currentUsername, receiver: username, type: "message"};
            clientRef.sendMessage("/support", JSON.stringify(messageToSend));
            setTimeout(() => {
                const messageToSend2 = {text: "", sender: currentUsername, receiver: username, type: "stopped typing"};
                clientRef.sendMessage("/support", JSON.stringify(messageToSend2));
            }, 100);
            setMessage('');
            setTyping(false);
            setIsLastMessageSeen(false);
            setTypingSend(false);
        }
    };

    useEffect(() => {
        if (messagesContainerRef.current) {
            messagesContainerRef.current.scrollTop = messagesContainerRef.current.scrollHeight;
        }
    }, [messages]);

    useEffect(() => {
        if (messagesContainerRef.current) {
            messagesContainerRef.current.scrollTop = messagesContainerRef.current.scrollHeight;
        }
    }, [isLastMessageSeen]);

    useEffect(() => {
        if (newMessage !== null && newMessage !== undefined) {
            if (newMessage.sender === username && newMessage.receiver === currentUsername) {
                if (newMessage.type === "message") {
                    console.log("text: " + newMessage.text +
                        " sender: " + newMessage.sender +
                        " receiver: " + newMessage.receiver +
                        " type: " + newMessage.type);

                    if (localStorage.getItem("messages") == null) localStorage.setItem("messages", JSON.stringify([]));
                    const allMessages = JSON.parse(localStorage.getItem("messages")) || [];
                    const updatedMessages = [...allMessages, newMessage];
                    localStorage.setItem("messages", JSON.stringify(updatedMessages));

                    const filteredMessages = updatedMessages.filter(
                        (msg) =>
                            (msg.receiver === username || msg.receiver === currentUsername) &&
                            (msg.sender === username || msg.sender === currentUsername)
                    );
                    setMessages(filteredMessages);

                    setNewMessage(null);
                    setIsLastMessageSeen(false);
                } else if (newMessage.type === "typing") {
                    console.log("typing: " + newMessage.text +
                        " sender: " + newMessage.sender +
                        " receiver: " + newMessage.receiver +
                        " type: " + newMessage.type);
                    setTyping(true);
                } else if (newMessage.type === "stopped typing") {
                    console.log("stopped typing: " + newMessage.text +
                        " sender: " + newMessage.sender +
                        " receiver: " + newMessage.receiver +
                        " type: " + newMessage.type);
                    setTyping(false);
                } else if (newMessage.type === "seen") {
                    console.log("seen: " + newMessage.text +
                        " sender: " + newMessage.sender +
                        " receiver: " + newMessage.receiver +
                        " type: " + newMessage.type);
                    setIsLastMessageSeen(true);
                }
            }
        }
    }, [newMessage, username, currentUsername]);

    function changeMessage(e) {
        const typedText = e.target.value;
        // console.log("Current text for chat: " + username + ", text:" + typedText);
        if (typedText !== "" && !typingSend) {
            const messageToSend = {text: typedText, sender: currentUsername, receiver: username, type: "typing"};
            clientRef.sendMessage("/support", JSON.stringify(messageToSend));
            setTypingSend(true);
        } else if (typedText === "") {
            // console.log("typing false");
            const messageToSend = {text: "", sender: currentUsername, receiver: username, type: "stopped typing"};
            clientRef.sendMessage("/support", JSON.stringify(messageToSend));
            setTypingSend(false);
        }
        setMessage(e.target.value);
    }

    function handleFocus() {
        // console.log("chat " + username + " focused");
        setIsFocused(true);
        const messageToSend = {text: "", sender: currentUsername, receiver: username, type: "seen"};
        clientRef.sendMessage("/support", JSON.stringify(messageToSend));
    }

    function handleBlur() {
        // console.log("chat " + username + " blurred");
        setIsFocused(false);
    }

    return <div className="bg-gray-50 p-4 rounded-md shadow-md flex flex-col h-96"
                onFocus={handleFocus}
                onBlur={handleBlur}>
        {/* Header */}
        <div className="flex flex-col justify-between mb-2">
            <div className="flex flex-row justify-between">
                <div className="">
                    <h2 className="font-bold text-lg">Chat with {username}</h2>
                </div>
                <div className="flex items-center">
                    <IoClose
                        className="hover:cursor-pointer text-2xl"
                        height="25"
                        width="25"
                        onClick={toggleChat}
                    />
                </div>
            </div>
            {typing && <div className="flex flex-row justify-start">
                <div className="">
                    <p className="text-sm">Typing...</p>
                </div>
            </div>}
        </div>

        {/* Messages Area (scrollable) */}
        <div ref={messagesContainerRef}
             className="flex-1 overflow-y-auto mb-2 border-2 rounded-md">
            {messages.map((msg, index) => (
                <div
                    key={index}
                    className={`text-white mb-1 ${
                        msg.sender === currentUsername ? 'text-right' : 'text-left'
                    }`}
                >
                    <div
                        className={`inline-block p-2 rounded-md ${
                            msg.sender === currentUsername ? 'bg-blue-500' : 'bg-gray-500'
                        }`}
                    >
                        {msg.text}
                    </div>
                    {msg.sender === currentUsername && isLastMessageSeen && index === messages.length - 1 && (
                        <div>
                            <span className="text-xs text-gray-500">seen</span>
                        </div>
                    )}
                </div>
            ))}
        </div>

        {/* Footer with Text Input and Send Button */}
        <div className="flex items-center flex-row">
            <input
                type="text"
                className="p-2 mr-2 rounded-md border-2 w-2/3"
                placeholder="Type your message..."
                value={message}
                onChange={changeMessage}
            />
            <button
                className="bg-blue-500 text-white p-2 rounded-md w-1/3"
                onClick={handleSendMessage}
            >
                Send
            </button>
        </div>
    </div>;
}