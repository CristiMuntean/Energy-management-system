import {useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {API_URLS} from "../utils/API_URLS";
import axios from "axios";
import {BsChevronDown, BsChevronUp} from "react-icons/bs";
import {IoClose} from "react-icons/io5";
import SockJsClient from "react-stomp";
import SupportChat from "./SupportChat";
import AdminChatList from "./AdminChatList";

export default function Profile() {
    const [user, setUser] = useState({
        username: "",
        password: "",
        role: ""
    });
    const [messages, setMessages] = useState([]);
    const SOCKET_URL = 'http://localhost:8083/ws-notification'

    const [devices, setDevices] = useState([]);
    const [users, setUsers] = useState([]);
    const [isOptionsDropdownOpen, setIsOptionsDropdownOpen] = useState(false);
    const [isDevicesDropdownOpen, setIsDevicesDropdownOpen] = useState(
        localStorage.getItem("isDevicesDropdownOpen") ? JSON.parse(localStorage.getItem("isDevicesDropdownOpen")) : false
    );
    const [isUsersDropdownOpen, setIsUsersDropdownOpen] = useState(
        localStorage.getItem("isUsersDropdownOpen") ? JSON.parse(localStorage.getItem("isUsersDropdownOpen")) : false
    );

    const [isCreateDeviceModalOpen, setIsCreateDeviceModalOpen] = useState(false);
    const [isDeleteDeviceModalOpen, setIsDeleteDeviceModalOpen] = useState(false);
    const [isUpdateDeviceModalOpen, setIsUpdateDeviceModalOpen] = useState(false);
    const [isUpdateDeviceFieldsModalOpen, setIsUpdateDeviceFieldsModalOpen] = useState(false);
    const [isLinkDeviceModalOpen, setIsLinkDeviceModalOpen] = useState(false);
    const [isUnlinkDeviceModalOpen, setIsUnlinkDeviceModalOpen] = useState(false);
    const [isCreateUserModalOpen, setIsCreateUserModalOpen] = useState(false);
    const [isDeleteUserModalOpen, setIsDeleteUserModalOpen] = useState(false);
    const [isUpdateUserModalOpen, setIsUpdateUserModalOpen] = useState(false);
    const [isUpdateUserFieldsModalOpen, setIsUpdateUserFieldsModalOpen] = useState(false);
    const [isDeleteAccountModalOpen, setIsDeleteAccountModalOpen] = useState(false);

    const [createDeviceDescription, setCreateDeviceDescription] = useState("");
    const [createDeviceAddress, setcreateDeviceAddress] = useState("");
    const [createDeviceMaxHourlyEnergyConsumption, setCreateDeviceMaxHourlyEnergyConsumption] = useState("");

    const [deletedDeviceSelectedId, setDeletedDeviceSelectedId] = useState(-1);
    const [updatedDeviceSelectedId, setUpdatedDeviceSelectedId] = useState(-1);

    const [linkedDeviceSelectedId, setLinkedDeviceSelectedId] = useState(-1);
    const [linkedUserSelectedId, setLinkedUserSelectedId] = useState(-1);
    const [unlinkedDeviceSelectedId, setUnlinkedDeviceSelectedId] = useState(-1);

    const [createUserUsername, setCreateUserUsername] = useState("");
    const [createUserPassword, setCreateUserPassword] = useState("");
    const [createUserRole, setCreateUserRole] = useState("ADMIN");

    const [updatedDeviceDescription, setUpdatedDeviceDescription] = useState("");
    const [updatedDeviceAddress, setUpdatedDeviceAddress] = useState("");
    const [updatedDeviceMaxHourlyEnergyConsumption, setUpdatedDeviceMaxHourlyEnergyConsumption] = useState("");

    const [deletedUserSelectedId, setDeletedUserSelectedId] = useState(-1);
    const [updatedUserSelectedId, setUpdatedUserSelectedId] = useState(-1);

    const [updateUserUsername, setUpdateUserUsername] = useState("");
    const [updateUserPassword, setUpdateUserPassword] = useState("");
    const [updateUserRole, setUpdateUserRole] = useState("ADMIN");

    const [createDeviceErrorMessage, setCreateDeviceErrorMessage] = useState("");
    const [updateDeviceErrorMessage, setUpdateDeviceErrorMessage] = useState("");
    const [deleteDeviceErrorMessage, setDeleteDeviceErrorMessage] = useState("");
    const [linkDeviceErrorMessage, setLinkDeviceErrorMessage] = useState("");
    const [unlinkDeviceErrorMessage, setUnlinkDeviceErrorMessage] = useState("");
    const [createUserErrorMessage, setCreateUserErrorMessage] = useState("");
    const [updateUserErrorMessage, setUpdateUserErrorMessage] = useState("");
    const [deleteUserErrorMessage, setDeleteUserErrorMessage] = useState("");

    const [chatList, setChatList] = useState([]);
    const navigate = useNavigate();

    const toggleDevicesDropdown = () => {
        setIsDevicesDropdownOpen(!isDevicesDropdownOpen);
    }

    useEffect(() => {
        localStorage.setItem("isDevicesDropdownOpen", JSON.stringify(isDevicesDropdownOpen));
    }, [isDevicesDropdownOpen]);

    const toggleUsersDropdown = () => {
        setIsUsersDropdownOpen(!isUsersDropdownOpen);
    }

    useEffect(() => {
        localStorage.setItem("isUsersDropdownOpen", JSON.stringify(isUsersDropdownOpen));
    }, [isUsersDropdownOpen]);

    const toggleOptionsDropdown = () => {
        setIsOptionsDropdownOpen(!isOptionsDropdownOpen);
    }

    const onConnected = () => {
        console.log('Connected!!!');
    }

    const onMessageReceived = (msg) => {
        console.log('Message received: ', msg);
        const userDevices = [...devices];

        const deviceIndex = userDevices.findIndex((device) => device.id === msg.deviceId);
        if (deviceIndex !== -1) {
            const newMessage = userDevices[deviceIndex].description + ": " + msg.message;
            setMessages((prevMessages) => [newMessage, ...prevMessages]);
            console.log(messages);
        }
    }

    const onDisconnected = () => {
        console.log('Disconnected!!!');
    }

    const removeMessage = (index) => {
        setMessages((prevMessages) => prevMessages.filter((message, i) => i !== index));
    }

    const toggleDeleteAccountModal = () => {
        if (isOptionsDropdownOpen) toggleOptionsDropdown();
        setIsDeleteAccountModalOpen(!isDeleteAccountModalOpen);
    }

    const toggleCreateDeviceModal = () => {
        setIsCreateDeviceModalOpen(!isCreateDeviceModalOpen);
        if (!isCreateDeviceModalOpen) {
            setCreateDeviceDescription("");
            setcreateDeviceAddress("");
            setCreateDeviceMaxHourlyEnergyConsumption("");
        }
    }

    const toggleDeleteDeviceModal = () => {
        setIsDeleteDeviceModalOpen(!isDeleteDeviceModalOpen);
        if (!isDeleteDeviceModalOpen) {
            setDeletedDeviceSelectedId(-1);
        }
    }

    const toggleUpdateDeviceModal = () => {
        setIsUpdateDeviceModalOpen(!isUpdateDeviceModalOpen);
        if (!isUpdateDeviceModalOpen) {
            setUpdatedDeviceSelectedId(-1);
        }
    }

    const toggleUpdateDeviceFieldsModal = () => {
        setIsUpdateDeviceFieldsModalOpen(!isUpdateDeviceFieldsModalOpen);
    }

    const toggleLinkDeviceModal = () => {
        setIsLinkDeviceModalOpen(!isLinkDeviceModalOpen);
        if (!isLinkDeviceModalOpen) {
            setLinkedDeviceSelectedId(-1);
            setLinkedUserSelectedId(-1);
        }
    }

    const toggleUnlinkDeviceModal = () => {
        setIsUnlinkDeviceModalOpen(!isUnlinkDeviceModalOpen);
        if (!isUnlinkDeviceModalOpen) {
            setUnlinkedDeviceSelectedId(-1);
        }
    }

    const toggleCreateUserModal = () => {
        setIsCreateUserModalOpen(!isCreateUserModalOpen);
        if (!isCreateUserModalOpen) {
            setCreateUserUsername("");
            setCreateUserPassword("");
        }
    }

    const toggleDeleteUserModal = () => {
        setIsDeleteUserModalOpen(!isDeleteUserModalOpen);
        if (!isDeleteUserModalOpen) {
            setDeletedUserSelectedId(-1);
        }
    }

    const toggleUpdateUserModal = () => {
        setIsUpdateUserModalOpen(!isUpdateUserModalOpen);
        if (!isUpdateUserModalOpen) {
            setUpdatedUserSelectedId(-1);
        }
    }

    const toggleUpdateUserFieldsModal = () => {
        setIsUpdateUserFieldsModalOpen(!isUpdateUserFieldsModalOpen);
    }

    useEffect(() => {
        const tempIsUsersDropdownOpen = JSON.parse(localStorage.getItem("isUsersDropdownOpen"));
        setIsUsersDropdownOpen(tempIsUsersDropdownOpen);
        const tempIsDevicesDropdownOpen = JSON.parse(localStorage.getItem("isDevicesDropdownOpen"));
        setIsDevicesDropdownOpen(tempIsDevicesDropdownOpen);
        const tempUser = localStorage.getItem("user");
        if (!tempUser) {
            localStorage.clear();
            navigate("/");
        }
        const tempDevices = localStorage.getItem("devices");
        const tempUsers = localStorage.getItem("users");

        try {
            setUser(JSON.parse(tempUser));
            setDevices(JSON.parse(tempDevices));
            setUsers(JSON.parse(tempUsers));

            const initialChatList = JSON.parse(tempUsers).filter(user => user.role === "CLIENT").map(user => {
                return {username: user.username};
            });
            console.log("initialChatList: ", initialChatList);
            setChatList(initialChatList);
            localStorage.setItem("activeChatList", JSON.stringify(initialChatList));
        } catch (e) {
            console.log(e);
        }
    }, []);

    const handleLogout = () => {
        localStorage.clear();
    }

    const handleDelete = () => {
        const user = JSON.parse(localStorage.getItem("user"));
        const token = JSON.parse(localStorage.getItem("accessToken"));
        axios.delete(API_URLS.USER_API_URL + "delete_user", {
            headers: {
                Authorization: "Bearer " + token.accessToken
            },
            data: {
                username: user.username,
                password: user.password
            }
        }).then(response => {
            console.log(response);
            localStorage.clear();
            navigate("/");
        }).catch(error => {
            console.log(error);
        })
    }

    function getTableWithDevices() {
        if (user.role === "CLIENT") {
            return <>
                <p className="text-center text-2xl pb-2">Your devices:</p>
                <table className="w-full border border-gray-300">
                    <thead>
                    <tr className="text-center">
                        <th className="px-4 py-2">Description</th>
                        <th className="px-4 py-2">Address</th>
                        <th className="px-4 py-2">Max hourly consumption</th>
                    </tr>
                    </thead>
                    <tbody>
                    {devices.map((device) => (
                        <tr key={device.id} className="text-center">
                            <td className="border px-4 py-2">{device.description}</td>
                            <td className="border px-4 py-2">{device.address}</td>
                            <td className="border px-4 py-2">{device.maxHourlyEnergyConsumption}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </>;
        } else {
            return <>
                <p className="text-center text-2xl pb-2">All devices:</p>
                <table className="w-full border border-gray-300">
                    <thead>
                    <tr>
                        <th className="px-4 py-2">Id</th>
                        <th className="px-4 py-2">Description</th>
                        <th className="px-4 py-2">Address</th>
                        <th className="px-4 py-2">Max hourly consumption</th>
                        <th className="px-4 py-2">Linked user id</th>
                    </tr>
                    </thead>
                    <tbody>
                    {devices.map((device) => (
                        <tr key={device.id} className="text-left">
                            <td className="border px-4 py-2">{device.id}</td>
                            <td className="border px-4 py-2">{device.description}</td>
                            <td className="border px-4 py-2">{device.address}</td>
                            <td className="border px-4 py-2">{device.maxHourlyEnergyConsumption}</td>
                            <td className="border px-4 py-2">{device.userId == null ? "No user linked to this device" : device.userId}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </>
        }
    }

    function getUsersTable() {
        if (user.role === "ADMIN") {
            return <div className="w-full">
                <p className="text-center text-2xl pb-2">All users:</p>
                <table className="w-full border border-gray-300">
                    <thead>
                    <tr className="text-center">
                        <th className="px-4 py-2">Id</th>
                        <th className="px-4 py-2">Username</th>
                        <th className="px-4 py-2">Role</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map((user) => (
                        <tr key={user.id} className="text-left">
                            <td className="border px-4 py-2">{user.id}</td>
                            <td className="border px-4 py-2">{user.username}</td>
                            <td className="border px-4 py-2">{user.role}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>;
        }
    }

    function selectRowInDeleteDeviceTable(rowKey) {
        let device = devices.find(device => device.id === rowKey);
        console.log("Selected device: " + device.id + ", " + device.description + ", " + device.address + ", " + device.maxHourlyEnergyConsumption);
        setDeletedDeviceSelectedId(rowKey);
    }

    function selectRowInUpdateDeviceTable(rowKey) {
        let device = devices.find(device => device.id === rowKey);
        console.log("Selected device: " + device.id + ", " + device.description + ", " + device.address + ", " + device.maxHourlyEnergyConsumption);
        setUpdatedDeviceSelectedId(rowKey);
    }

    function selectRowInLinkDeviceTable(rowKey) {
        let device = devices.find(device => device.id === rowKey);
        console.log("Selected device: " + device.id + ", " + device.description + ", " + device.address + ", " + device.maxHourlyEnergyConsumption);
        setLinkedDeviceSelectedId(rowKey);
    }

    function selectRowInLinkUserTable(rowKey) {
        let user = users.find(user => user.id === rowKey);
        console.log("Selected user: " + user.id + ", " + user.username + ", " + user.role);
        setLinkedUserSelectedId(rowKey);
    }

    function selectRowInUnlinkDeviceTable(rowKey) {
        let device = devices.find(device => device.id === rowKey);
        console.log("Selected device: " + device.id + ", " + device.description + ", " + device.address + ", " + device.maxHourlyEnergyConsumption);
        setUnlinkedDeviceSelectedId(rowKey);
    }

    function selectRowInDeleteUserTable(rowKey) {
        let user = users.find(user => user.id === rowKey);
        console.log("Selected user: " + user.id + ", " + user.username + ", " + user.role);
        setDeletedUserSelectedId(rowKey);
    }

    function selectRowInUpdateUserTable(rowKey) {
        let user = users.find(user => user.id === rowKey);
        console.log("Selected user: " + user.id + ", " + user.username + ", " + user.role);
        setUpdatedUserSelectedId(rowKey);
    }

    function deepEqual(object1, object2) {
        if (object1 === object2) {
            return true;
        }
        if (object1 === null || object2 === null || typeof object1 !== 'object' || typeof object2 !== 'object') {
            return false;
        }
        let keys1 = Object.keys(object1);
        let keys2 = Object.keys(object2);
        if (keys1.length !== keys2.length) {
            return false;
        }
        for (let key of keys1) {
            if (!keys2.includes(key) || !deepEqual(object1[key], object2[key])) {
                return false;
            }
        }
        return true;
    }

    function getModal() {
        function getCreateDeviceModal() {

            function sendCreateRequest(description, address, maxHourlyEnergyConsumption) {
                console.log("Device: " + description + ", " + address + ", " + maxHourlyEnergyConsumption);
                axios.post(API_URLS.DEVICE_API_URL + 'create_device', {
                    description: description,
                    address: address,
                    maxHourlyEnergyConsumption: maxHourlyEnergyConsumption
                }, {
                    headers: {
                        Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken
                    }
                }).then(response => {
                    console.log(response);
                    const responseDevice = response.data;
                    console.log(responseDevice);
                    devices.push(responseDevice);
                    localStorage.setItem("devices", JSON.stringify(devices));
                    toggleCreateDeviceModal();
                }).catch(error => {
                    console.log(error);
                    setCreateDeviceErrorMessage(error.response.data);
                });
            }

            return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                <div className="relative w-1/3 h-1/2 bg-white rounded-md mx-auto my-auto p-5">
                    <div className="flex justify-between">
                        <h1 className="font-bold text-2xl">Create device</h1>
                        <div className="flex items-center">
                            <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                     onClick={toggleCreateDeviceModal}/>
                        </div>
                    </div>
                    <form className="flex flex-col gap-y-2 text-left my-5">
                        <label htmlFor="description">Description</label>
                        <input type="text" id="description" name="description"
                               className='border-black border rounded-md'
                               onChange={(e) => setCreateDeviceDescription(e.target.value)}
                        />
                        <label htmlFor="address">Address</label>
                        <input type="text" id="address" name="address"
                               className='border-black border rounded-md'
                               onChange={(e) => setcreateDeviceAddress(e.target.value)}
                        />
                        <label htmlFor="maxHourlyEnergyConsumption">Max hourly energy consumption</label>
                        <input type="text" id="maxHourlyEnergyConsumption" name="maxHourlyEnergyConsumption"
                               className='border-black border rounded-md'
                               onChange={(e) => setCreateDeviceMaxHourlyEnergyConsumption(e.target.value)}
                        />
                        <p className="text-red-500 text-sm">{createDeviceErrorMessage}</p>
                    </form>
                    <button
                        className="absolute bottom-3 right-5 bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold my-3 py-2 px-4 rounded"
                        onClick={() => sendCreateRequest(createDeviceDescription, createDeviceAddress, createDeviceMaxHourlyEnergyConsumption)}>
                        Create
                    </button>
                </div>
            </div>;
        }

        function getDeleteDeviceModal() {
            function sendDeleteRequest() {
                const device = devices.find(device => device.id === deletedDeviceSelectedId);
                console.log("Device: " + device.id + ", " + device.description + ", " + device.address + ", " + device.maxHourlyEnergyConsumption);
                axios.delete(API_URLS.DEVICE_API_URL + 'delete_device', {
                    headers: {
                        Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken
                    },
                    data: {
                        id: device.id,
                        description: device.description,
                        address: device.address,
                        maxHourlyEnergyConsumption: device.maxHourlyEnergyConsumption
                    }
                }).then(response => {
                    console.log(response);
                    const responseDevice = response.data;
                    console.log(responseDevice);
                    devices.splice(devices.findIndex(device => device.id === deletedDeviceSelectedId), 1);
                    localStorage.setItem("devices", JSON.stringify(devices));
                    toggleDeleteDeviceModal();
                }).catch(error => {
                    console.log(error);
                    setDeleteDeviceErrorMessage(error.response.data);
                });
            }

            return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                <div
                    className="w-1/2 h-2/3 bg-white rounded-md mx-auto my-auto p-5 overflow-y-scroll flex flex-col no-scrollbar">
                    <div className="flex justify-between pb-4">
                        <h1 className="font-bold text-2xl">Delete device</h1>
                        <div className="flex items-center">
                            <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                     onClick={toggleDeleteDeviceModal}/>
                        </div>
                    </div>
                    <div className="flex flex-1">
                        <table className="w-full border border-gray-300">
                            <thead>
                            <tr>
                                <th className="px-4 py-2">Id</th>
                                <th className="px-4 py-2">Description</th>
                                <th className="px-4 py-2">Address</th>
                                <th className="px-4 py-2">Max hourly consumption</th>
                            </tr>
                            </thead>
                            <tbody>
                            {devices.map((device) => (
                                <tr key={device.id}
                                    className={`${device.id === deletedDeviceSelectedId ? "bg-gray-200 hover:bg-gray-300" : "hover:bg-gray-100"}`}
                                    onClick={() => selectRowInDeleteDeviceTable(device.id)}>
                                    <td className="border px-4 py-2">{device.id}</td>
                                    <td className="border px-4 py-2">{device.description}</td>
                                    <td className="border px-4 py-2">{device.address}</td>
                                    <td className="border px-4 py-2">{device.maxHourlyEnergyConsumption}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                    <p className="text-red-500 text-sm">{deleteDeviceErrorMessage}</p>
                    <div className="w-full pt-4 flex justify-end">
                        <button
                            className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded"
                            onClick={sendDeleteRequest}>
                            Delete
                        </button>
                    </div>
                </div>
            </div>;
        }

        function getUpdateDeviceModal() {

            function getUpdateDeviceFieldsModal() {
                function sendUpdateRequest(description, address, maxHourlyEnergyConsumption) {
                    const initialDevice = devices.find(device => device.id === updatedDeviceSelectedId);
                    const updatedDevice = {...initialDevice};
                    console.log("Device: " + initialDevice.id + ", " + description + ", " + address + ", " + maxHourlyEnergyConsumption);
                    if (description !== "") {
                        updatedDevice.description = description;
                    }
                    if (address !== "") {
                        updatedDevice.address = address;
                    }
                    if (maxHourlyEnergyConsumption !== "") {
                        updatedDevice.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
                    }
                    updatedDevice.id = initialDevice.id;
                    console.log("Updated device: " + updatedDevice.id + ", " + updatedDevice.description + ", " + updatedDevice.address + ", " + updatedDevice.maxHourlyEnergyConsumption);
                    if (!deepEqual(initialDevice, updatedDevice)) {
                        axios.put(API_URLS.DEVICE_API_URL + 'update_device', {
                            id: updatedDevice.id,
                            description: updatedDevice.description,
                            address: updatedDevice.address,
                            maxHourlyEnergyConsumption: updatedDevice.maxHourlyEnergyConsumption
                        }, {
                            headers: {
                                Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken
                            }
                        }).then(response => {
                            console.log(response);
                            const responseDevice = response.data;
                            console.log(responseDevice);
                            devices.splice(devices.findIndex(device => device.id === updatedDeviceSelectedId), 1, updatedDevice);
                            setDevices(devices);
                            localStorage.setItem("devices", JSON.stringify(devices));
                            toggleUpdateDeviceFieldsModal();
                            toggleUpdateDeviceModal();
                        }).catch(error => {
                            console.log(error);
                            setUpdateDeviceErrorMessage(error.response.data);
                        });
                    }
                }

                return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                    <div className="relative w-1/3 h-1/2 bg-white rounded-md mx-auto my-auto p-5">
                        <div className="flex justify-between">
                            <h1 className="font-bold text-2xl">Complete fields that need to be updated</h1>
                            <div className="flex items-center">
                                <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                         onClick={toggleUpdateDeviceFieldsModal}/>
                            </div>
                        </div>
                        <form className="flex flex-col gap-y-2 text-left my-5">
                            <label htmlFor="description">Description</label>
                            <input type="text" id="description" name="description"
                                   className='border-black border rounded-md'
                                   onChange={(e) => setUpdatedDeviceDescription(e.target.value)}
                            />
                            <label htmlFor="address">Address</label>
                            <input type="text" id="address" name="address"
                                   className='border-black border rounded-md'
                                   onChange={(e) => setUpdatedDeviceAddress(e.target.value)}
                            />
                            <label htmlFor="maxHourlyEnergyConsumption">Max hourly energy consumption</label>
                            <input type="text" id="maxHourlyEnergyConsumption" name="maxHourlyEnergyConsumption"
                                   className='border-black border rounded-md'
                                   onChange={(e) => setUpdatedDeviceMaxHourlyEnergyConsumption(e.target.value)}
                            />
                            <p className="text-red-500 text-sm">{updateDeviceErrorMessage}</p>
                        </form>
                        <button
                            className="absolute bottom-3 right-5 bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold my-3 py-2 px-4 rounded"
                            onClick={() => sendUpdateRequest(updatedDeviceDescription, updatedDeviceAddress, updatedDeviceMaxHourlyEnergyConsumption)}>
                            Update
                        </button>
                    </div>
                </div>;
            }

            return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                {(isUpdateDeviceFieldsModalOpen &&
                        getUpdateDeviceFieldsModal()) ||
                    <div
                        className="w-1/2 h-2/3 bg-white rounded-md mx-auto my-auto p-5 overflow-y-scroll flex flex-col no-scrollbar">
                        <div className="flex justify-between pb-4">
                            <h1 className="font-bold text-2xl">Select device to update</h1>
                            <div className="flex items-center">
                                <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                         onClick={toggleUpdateDeviceModal}/>
                            </div>
                        </div>
                        <div className="flex flex-1">
                            <table className="w-full border border-gray-300">
                                <thead>
                                <tr>
                                    <th className="px-4 py-2">Id</th>
                                    <th className="px-4 py-2">Description</th>
                                    <th className="px-4 py-2">Address</th>
                                    <th className="px-4 py-2">Max hourly consumption</th>
                                </tr>
                                </thead>
                                <tbody>
                                {devices.map((d) => (
                                    <tr key={d.id}
                                        className={`${d.id === updatedDeviceSelectedId ? "bg-gray-200 hover:bg-gray-300" : "hover:bg-gray-100"}`}
                                        onClick={() => selectRowInUpdateDeviceTable(d.id)}>
                                        <td className="border px-4 py-2">{d.id}</td>
                                        <td className="border px-4 py-2">{d.description}</td>
                                        <td className="border px-4 py-2">{d.address}</td>
                                        <td className="border px-4 py-2">{d.maxHourlyEnergyConsumption}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                        <div className="w-full pt-4 flex justify-end">
                            <button
                                className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded"
                                onClick={toggleUpdateDeviceFieldsModal}>
                                Select
                            </button>
                        </div>
                    </div>
                }
            </div>;
        }

        function getLinkDeviceModal() {
            function sendLinkRequest() {
                const device = devices.find(device => device.id === linkedDeviceSelectedId);
                const user = users.find(user => user.id === linkedUserSelectedId);
                console.log("Device: " + device.id + ", " + device.description + ", " + device.address + ", " + device.maxHourlyEnergyConsumption);
                console.log("User: " + user.id + ", " + user.username + ", " + user.role);
                axios.post(API_URLS.DEVICE_API_URL + 'link_device_to_user', {
                    deviceId: device.id,
                    userId: user.id
                }, {
                    headers: {
                        Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken
                    }
                }).then(response => {
                    console.log(response);
                    device.userId = user.id;
                    devices.splice(devices.findIndex(device => device.id === linkedDeviceSelectedId), 1, device);
                    localStorage.setItem("devices", JSON.stringify(devices));
                    toggleLinkDeviceModal();
                }).catch(error => {
                    console.log(error);
                    setLinkDeviceErrorMessage(error.response.data);
                });
            }

            return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                <div
                    className="w-1/2 h-2/3 bg-white rounded-md mx-auto my-auto p-5 overflow-y-scroll flex flex-col no-scrollbar">
                    <div className="flex justify-between pb-4">
                        <h1 className="font-bold text-2xl">Link device</h1>
                        <div className="flex items-center">
                            <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                     onClick={toggleLinkDeviceModal}/>
                        </div>
                    </div>
                    <div className="flex flex-1">
                        <table className="w-full border border-gray-300">
                            <thead>
                            <tr>
                                <th className="px-4 py-2">Id</th>
                                <th className="px-4 py-2">Description</th>
                                <th className="px-4 py-2">Address</th>
                                <th className="px-4 py-2">Max hourly consumption</th>
                            </tr>
                            </thead>
                            <tbody>
                            {devices.map((device) => (
                                <tr key={device.id}
                                    className={`${device.id === linkedDeviceSelectedId ? "bg-gray-200 hover:bg-gray-300" : "hover:bg-gray-100"}`}
                                    onClick={() => selectRowInLinkDeviceTable(device.id)}>
                                    <td className="border px-4 py-2">{device.id}</td>
                                    <td className="border px-4 py-2">{device.description}</td>
                                    <td className="border px-4 py-2">{device.address}</td>
                                    <td className="border px-4 py-2">{device.maxHourlyEnergyConsumption}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                    <div className="flex flex-1">
                        <table className="w-full border border-gray-300">
                            <thead>
                            <tr>
                                <th className="px-4 py-2">Id</th>
                                <th className="px-4 py-2">Username</th>
                                <th className="px-4 py-2">Role</th>
                            </tr>
                            </thead>
                            <tbody>
                            {users.filter((user) => user.role === "CLIENT").map((user) => (
                                <tr key={user.id}
                                    className={`${user.id === linkedUserSelectedId ? "bg-gray-200 hover:bg-gray-300" : "hover:bg-gray-100"}`}
                                    onClick={() => selectRowInLinkUserTable(user.id)}>
                                    <td className="border px-4 py-2">{user.id}</td>
                                    <td className="border px-4 py-2">{user.username}</td>
                                    <td className="border px-4 py-2">{user.role}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                        <p className="text-red-500 text-sm">{linkDeviceErrorMessage}</p>
                    </div>
                    <div className="w-full pt-4 flex justify-end">
                        <button
                            className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded"
                            onClick={sendLinkRequest}>
                            Link device to user
                        </button>
                    </div>
                </div>
            </div>;
        }

        function getUnlinkDeviceModal() {
            function sendUnlinkRequest() {
                const device = devices.find(device => device.id === unlinkedDeviceSelectedId);
                console.log("Device: " + device.id + ", " + device.description + ", " + device.address + ", " + device.maxHourlyEnergyConsumption);
                axios.delete(API_URLS.DEVICE_API_URL + 'unlink_device_from_user', {
                    headers: {
                        Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken
                    },
                    data: {
                        deviceId: device.id,
                        userId: device.userId
                    }
                }).then(response => {
                    console.log(response);
                    device.userId = null;
                    devices.splice(devices.findIndex(device => device.id === unlinkedDeviceSelectedId), 1, device);
                    localStorage.setItem("devices", JSON.stringify(devices));
                    toggleUnlinkDeviceModal();
                }).catch(error => {
                    console.log(error);
                    setUnlinkDeviceErrorMessage(error.response.data);
                });
            }

            return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                <div
                    className="w-1/2 h-2/3 bg-white rounded-md mx-auto my-auto p-5 overflow-y-scroll flex flex-col no-scrollbar">
                    <div className="flex justify-between pb-4">
                        <h1 className="font-bold text-2xl">Unlink device</h1>
                        <div className="flex items-center">
                            <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                     onClick={toggleUnlinkDeviceModal}/>
                        </div>
                    </div>
                    <div className="flex flex-1">
                        <table className="w-full border border-gray-300">
                            <thead>
                            <tr>
                                <th className="px-4 py-2">Id</th>
                                <th className="px-4 py-2">Description</th>
                                <th className="px-4 py-2">Address</th>
                                <th className="px-4 py-2">Max hourly consumption</th>
                                <th className="px-4 py-2">User Id</th>
                            </tr>
                            </thead>
                            <tbody>
                            {devices.filter((device) => device.userId != null).map((device) => (
                                <tr key={device.id}
                                    className={`${device.id === unlinkedDeviceSelectedId ? "bg-gray-200 hover:bg-gray-300" : "hover:bg-gray-100"}`}
                                    onClick={() => selectRowInUnlinkDeviceTable(device.id)}>
                                    <td className="border px-4 py-2">{device.id}</td>
                                    <td className="border px-4 py-2">{device.description}</td>
                                    <td className="border px-4 py-2">{device.address}</td>
                                    <td className="border px-4 py-2">{device.maxHourlyEnergyConsumption}</td>
                                    <td className="border px-4 py-2">{device.userId}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                        <p className="text-red-500 text-sm">{unlinkDeviceErrorMessage}</p>
                    </div>
                    <div className="w-full pt-4 flex justify-end">
                        <button
                            className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded"
                            onClick={sendUnlinkRequest}>
                            Unlink device from user
                        </button>
                    </div>
                </div>
            </div>;
        }

        function getCreateUserModal() {
            function sendCreateRequest(username, password, role) {
                console.log("User: " + username + ", " + password + ", " + role);
                const accessToken = JSON.parse(localStorage.getItem("accessToken"));
                axios.post(API_URLS.USER_API_URL + 'register', {
                    username,
                    password,
                    role
                }, {
                    headers: {
                        Authorization: "Bearer " + accessToken.accessToken
                    }
                }).then(response => {
                    const newUser = {
                        'id': response.data,
                        'username': username,
                        'password': password,
                        'role': role,
                        'links': []
                    };
                    users.push(newUser);
                    localStorage.setItem("users", JSON.stringify(users));

                    const newList = [...chatList, {username: username}];
                    console.log("newList: ", newList);
                    setChatList((prevChatList) => [...prevChatList, {username: username}]);
                    localStorage.setItem("activeChatList", JSON.stringify(newList));

                    toggleCreateUserModal();
                }).catch(error => {
                    console.log(error);
                    setCreateUserErrorMessage(error.response.data);
                });
            }

            return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                <div className="relative w-1/3 h-1/2 bg-white rounded-md mx-auto my-auto p-5">
                    <div className="flex justify-between">
                        <h1 className="font-bold text-2xl">Create user</h1>
                        <div className="flex items-center">
                            <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                     onClick={toggleCreateUserModal}/>
                        </div>
                    </div>
                    <form className="flex flex-col gap-y-2 text-left my-5">
                        <label htmlFor="username">Username</label>
                        <input type="text" id="username" name="username"
                               className='border-black border rounded-md'
                               onChange={(e) => setCreateUserUsername(e.target.value)}
                        />
                        <label htmlFor="password">Password</label>
                        <input type="password" id="password" name="password"
                               className='border-black border rounded-md'
                               onChange={(e) => setCreateUserPassword(e.target.value)}
                        />
                        <label htmlFor="role">Role</label>
                        <select id="role" name="role"
                                className='border-black border rounded-md'
                                value={createUserRole}
                                onChange={(e) => setCreateUserRole(e.target.value)}>
                            <option value="ADMIN">ADMIN</option>
                            <option value="CLIENT">CLIENT</option>
                        </select>
                    </form>
                    <p className="text-red-500 text-sm">{createUserErrorMessage}</p>
                    <button
                        className="absolute bottom-3 right-5 bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold my-3 py-2 px-4 rounded"
                        onClick={() => sendCreateRequest(createUserUsername, createUserPassword, createUserRole)}>
                        Create
                    </button>
                </div>
            </div>;
        }

        function getDeleteUserModal() {
            function sendDeleteRequest() {
                const deletedUser = users.find(user => user.id === deletedUserSelectedId);
                const token = JSON.parse(localStorage.getItem("accessToken"));
                console.log("User: " + deletedUser.id + ", " + deletedUser.username + ", " + deletedUser.role);
                axios.delete(API_URLS.USER_API_URL + 'delete_user', {
                    data: {
                        id: deletedUser.id,
                        username: deletedUser.username,
                        password: deletedUser.password,
                        role: deletedUser.role
                    },
                    headers: {
                        Authorization: "Bearer " + token.accessToken
                    }
                }).then(response => {
                    console.log(response);
                    users.splice(users.findIndex(user => user.id === deletedUserSelectedId), 1);
                    setUsers(users);
                    localStorage.setItem("users", JSON.stringify(users));

                    devices.filter(device => device.userId === deletedUserSelectedId).map(device => {
                        device.userId = null;
                    });

                    const newList = chatList.filter(user => user.username !== deletedUser.username);
                    console.log("newList: ", newList);
                    setChatList(chatList.filter(user => user.username !== deletedUser.username));
                    localStorage.setItem("activeChatList", JSON.stringify(newList));

                    toggleDeleteUserModal();
                }).catch(error => {
                    console.log(error);
                    setDeleteUserErrorMessage(error.response.data);
                });
            }

            return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                <div
                    className="w-1/2 h-2/3 bg-white rounded-md mx-auto my-auto p-5 overflow-y-scroll flex flex-col no-scrollbar">
                    <div className="flex justify-between pb-4">
                        <h1 className="font-bold text-2xl">Delete user</h1>
                        <div className="flex items-center">
                            <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                     onClick={toggleDeleteUserModal}/>
                        </div>
                    </div>
                    <div className="flex flex-1">
                        <table className="w-full border border-gray-300">
                            <thead>
                            <tr>
                                <th className="px-4 py-2">Id</th>
                                <th className="px-4 py-2">Username</th>
                                <th className="px-4 py-2">Role</th>
                            </tr>
                            </thead>
                            <tbody>
                            {users.filter((deletedUser) => deletedUser.id !== user.id).map((u) => (
                                <tr key={u.id}
                                    className={`${u.id === deletedUserSelectedId ? "bg-gray-200 hover:bg-gray-300" : "hover:bg-gray-100"}`}
                                    onClick={() => selectRowInDeleteUserTable(u.id)}>
                                    <td className="border px-4 py-2">{u.id}</td>
                                    <td className="border px-4 py-2">{u.username}</td>
                                    <td className="border px-4 py-2">{u.role}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                    <p className="text-red-500 text-sm">{deleteUserErrorMessage}</p>
                    <div className="w-full pt-4 flex justify-end">
                        <button
                            className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded"
                            onClick={sendDeleteRequest}>
                            Delete
                        </button>
                    </div>
                </div>
            </div>;
        }

        function getUpdateUserModal() {

            function getUpdateUserFieldsModal() {
                function sendUpdateRequest(username, password, role) {
                    const initialUser = users.find(user => user.id === updatedUserSelectedId);
                    const updatedUser = {...initialUser};
                    const token = JSON.parse(localStorage.getItem("accessToken"));
                    if (username !== "") {
                        updatedUser.username = username;
                    }
                    if (password !== "") {
                        updatedUser.password = password;
                    }
                    updatedUser.role = role;
                    console.log("User: " + updatedUser.id + ", " + updatedUser.username + ", " + updatedUser.password + ", " + updatedUser.role);
                    if (!deepEqual(initialUser, updatedUser)) {
                        axios.put(API_URLS.USER_API_URL + 'update_user', {
                            id: updatedUser.id,
                            username: updatedUser.username,
                            password: updatedUser.password,
                            role: updatedUser.role
                        }, {
                            headers: {
                                Authorization: "Bearer " + token.accessToken
                            }
                        }).then(response => {
                            console.log(response);
                            users.splice(users.findIndex(user => user.id === updatedUserSelectedId), 1, updatedUser);
                            setUsers(users);
                            localStorage.setItem("users", JSON.stringify(users));

                            if (updatedUser.username !== initialUser.username) {
                                const newList = chatList.map(user => {
                                    if (user.username === initialUser.username) {
                                        user.username = updatedUser.username;
                                    }
                                    return user;
                                });
                                setChatList(chatList.map(user => {
                                    if (user.username === initialUser.username) {
                                        user.username = updatedUser.username;
                                    }
                                    return user;
                                }));
                                localStorage.setItem("activeChatList", JSON.stringify(newList));
                            }

                            toggleUpdateUserModal();
                        }).catch(error => {
                            console.log(error);
                            setUpdateUserErrorMessage(error.response.data);
                        });
                    }
                }

                return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                    <div className="relative w-1/3 h-1/2 bg-white rounded-md mx-auto my-auto p-5">
                        <div className="flex justify-between">
                            <h1 className="font-bold text-2xl">Complete fields that need to be updated</h1>
                            <div className="flex items-center">
                                <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                         onClick={toggleUpdateUserFieldsModal}/>
                            </div>
                        </div>
                        <form className="flex flex-col gap-y-2 text-left my-5">
                            <label htmlFor="username">Username</label>
                            <input type="text" id="username" name="username"
                                   className='border-black border rounded-md'
                                   onChange={(e) => setUpdateUserUsername(e.target.value)}
                            />
                            <label htmlFor="password">Password</label>
                            <input type="password" id="password" name="password"
                                   className='border-black border rounded-md'
                                   onChange={(e) => setUpdateUserPassword(e.target.value)}
                            />
                            <label htmlFor="role">Role</label>
                            <select id="role" name="role"
                                    className='border-black border rounded-md'
                                    value={updateUserRole}
                                    onChange={(e) => setUpdateUserRole(e.target.value)}>
                                <option value="ADMIN">ADMIN</option>
                                <option value="CLIENT">CLIENT</option>
                            </select>
                        </form>
                        <p className="text-red-500 text-sm">{updateUserErrorMessage}</p>
                        <button
                            className="absolute bottom-3 right-5 bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold my-3 py-2 px-4 rounded"
                            onClick={() => sendUpdateRequest(updateUserUsername, updateUserPassword, updateUserRole)}>
                            Update
                        </button>
                    </div>
                </div>;
            }

            return <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                {(isUpdateUserFieldsModalOpen &&
                        getUpdateUserFieldsModal()) ||
                    <div
                        className="w-1/2 h-2/3 bg-white rounded-md mx-auto my-auto p-5 overflow-y-scroll flex flex-col no-scrollbar">
                        <div className="flex justify-between pb-4">
                            <h1 className="font-bold text-2xl">Select user to update</h1>
                            <div className="flex items-center">
                                <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                         onClick={toggleUpdateUserModal}/>
                            </div>
                        </div>
                        <div className="flex flex-1">
                            <table className="w-full border border-gray-300">
                                <thead>
                                <tr>
                                    <th className="px-4 py-2">Id</th>
                                    <th className="px-4 py-2">Username</th>
                                    <th className="px-4 py-2">Role</th>
                                </tr>
                                </thead>
                                <tbody>
                                {users.filter((u) => u.id !== user.id).map((u) => (
                                    <tr key={u.id}
                                        className={`${u.id === updatedUserSelectedId ? "bg-gray-200 hover:bg-gray-300" : "hover:bg-gray-100"}`}
                                        onClick={() => selectRowInUpdateUserTable(u.id)}>
                                        <td className="border px-4 py-2">{u.id}</td>
                                        <td className="border px-4 py-2">{u.username}</td>
                                        <td className="border px-4 py-2">{u.role}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                        <div className="w-full pt-4 flex justify-end">
                            <button
                                className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded"
                                onClick={() => {
                                    setUpdateUserRole(users.find(user => user.id === updatedUserSelectedId).role);
                                    toggleUpdateUserFieldsModal();
                                }
                                }>
                                Select
                            </button>
                        </div>
                    </div>
                }
            </div>;
        }

        function getDeleteAccountModal() {
            return (
                <div className="w-screen h-screen fixed bg-black/40 flex items-center z-20">
                    <div className="flex flex-col w-1/3 bg-white rounded-md mx-auto my-auto p-5">
                        <div className="flex justify-center">
                            <h1 className="font-bold text-2xl">Are you sure you want to delete your account?</h1>
                        </div>
                        {/*<p className="text-red-500 text-sm">{deleteAccountErrorMessage}</p>*/}
                        <div className="flex justify-center flex-row">
                            <button
                                className="flex bg-red-600 hover:bg-red-700 text-white font-bold my-3 py-2 px-4 mx-10 rounded"
                                onClick={handleDelete}>
                                Yes
                            </button>
                            <button
                                className="flex bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold my-3 py-2 px-4 mx-10 rounded"
                                onClick={toggleDeleteAccountModal}>
                                No
                            </button>
                        </div>
                    </div>
                </div>);
        }

        return <>
            {/*modal*/}
            {
                (isCreateDeviceModalOpen &&
                    getCreateDeviceModal()
                ) ||
                (isUpdateDeviceModalOpen &&
                    getUpdateDeviceModal()
                ) ||
                (isDeleteDeviceModalOpen &&
                    getDeleteDeviceModal()
                ) ||
                (isLinkDeviceModalOpen &&
                    getLinkDeviceModal()
                ) ||
                (isUnlinkDeviceModalOpen &&
                    getUnlinkDeviceModal()
                ) ||
                (isCreateUserModalOpen &&
                    getCreateUserModal()
                ) ||
                (isUpdateUserModalOpen &&
                    getUpdateUserModal()
                ) ||
                (isDeleteUserModalOpen &&
                    getDeleteUserModal()
                ) ||
                (isDeleteAccountModalOpen &&
                    getDeleteAccountModal()
                )
            }
            {/*modal end*/}
        </>;
    }

    function getAdminSideNav() {
        return <>
            {user.role === 'ADMIN' && (
                <div className="w-60 bg-gray-200 p-4">
                    {/*<!-- Device Dropdown -->*/}
                    <div className="mb-4">

                        <div
                            className="flex flex-row w-full items-center justify-between hover:cursor-pointer hover:bg-gray-300 rounded px-2"
                            onClick={toggleDevicesDropdown}>
                            <div className="font-semibold mb-2">Devices</div>
                            {!isDevicesDropdownOpen ?
                                <BsChevronDown height="25" width="25"
                                               className="flex justify-center mx-2"/> :
                                <BsChevronUp height="25" width="25"
                                             className="flex justify-center mx-2"/>}
                        </div>
                        {isDevicesDropdownOpen && (
                            <div className="dropdown">
                                {/*<!-- Device buttons go here -->*/}
                                <button className="w-full flex justify-start hover:bg-gray-300 px-2 py-1 rounded-md"
                                        onClick={toggleCreateDeviceModal}>
                                    Create device
                                </button>
                                <button className="w-full flex justify-start hover:bg-gray-300 px-2 py-1 rounded-md"
                                        onClick={toggleUpdateDeviceModal}>
                                    Update device
                                </button>
                                <button className="w-full flex justify-start hover:bg-gray-300 px-2 py-1 rounded-md"
                                        onClick={toggleDeleteDeviceModal}>
                                    Delete device
                                </button>
                                <button className="w-full flex justify-start hover:bg-gray-300 px-2 py-1 rounded-md"
                                        onClick={toggleLinkDeviceModal}>
                                    Link device to user
                                </button>
                                <button className="w-full flex justify-start hover:bg-gray-300 px-2 py-1 rounded-md"
                                        onClick={toggleUnlinkDeviceModal}>
                                    Unlink device from user
                                </button>
                            </div>
                        )}
                    </div>
                    {/*<!-- User Dropdown -->*/}
                    <div>
                        <div
                            className="flex flex-row w-full items-center justify-between hover:cursor-pointer hover:bg-gray-300 hover:bg-gray-300 rounded px-2"
                            onClick={toggleUsersDropdown}>
                            <div className="font-semibold mb-2">
                                Users
                            </div>
                            {!isUsersDropdownOpen ?
                                <BsChevronDown height="25" width="25" className="flex justify-center mx-2"/> :
                                <BsChevronUp height="25" width="25" className="flex justify-center mx-2"/>}
                        </div>
                        {isUsersDropdownOpen && (
                            <div className="dropdown">
                                {/*<!-- User buttons go here -->*/}
                                <button className="w-full flex justify-start hover:bg-gray-300 px-2 py-1 rounded-md"
                                        onClick={toggleCreateUserModal}>
                                    Create user
                                </button>
                                <button className="w-full flex justify-start hover:bg-gray-300 px-2 py-1 rounded-md"
                                        onClick={toggleUpdateUserModal}>
                                    Update user
                                </button>
                                <button className="w-full flex justify-start hover:bg-gray-300 px-2 py-1 rounded-md"
                                        onClick={toggleDeleteUserModal}>
                                    Delete user
                                </button>
                            </div>
                        )}
                    </div>
                </div>)}
        </>;
    }

    function areAllModalsClosed() {
        return !isDeleteAccountModalOpen &&
            !isCreateDeviceModalOpen &&
            !isUpdateDeviceModalOpen &&
            !isDeleteDeviceModalOpen &&
            !isLinkDeviceModalOpen &&
            !isUnlinkDeviceModalOpen &&
            !isCreateUserModalOpen &&
            !isUpdateUserModalOpen &&
            !isDeleteUserModalOpen;
    }

    function getWarningModals() {
        function getWarningModal(message, index) {
            console.log("Warning modal: " + message + ", " + index);
            return (
                <div
                    key={index}
                    className={index > 0 ?
                        "w-full h-1/12 flex items-center relative mt-2" :
                        "w-full h-1/12 flex items-center relative"}>
                    <div
                        className="flex flex-row w-full h-full bg-red-500 rounded-md mx-auto my-auto p-3 justify-between">
                        <div className="flex justify-center">
                            <h1 className="font-bold text-sm text-white">{message}</h1>
                        </div>
                        <div className="flex items-start text-white inline justify-end">
                            <IoClose className="hover:cursor-pointer text-2xl" height="25" width="25"
                                     onClick={() => removeMessage(index)}/>
                        </div>
                    </div>
                </div>
            )
        }

        const windowHeight = window.innerHeight;
        //heightComp is the height of the div containing messages.length modals of height 1/10 of the screen
        const heightComp = (windowHeight / 11) * messages.length;
        const heightCompPx = heightComp + "px";
        return (
            <div style={{height: heightCompPx}}
                 className="fixed flex-col w-1/4 bottom-0 right-0 align-middle">
                {messages.map((message, index) => getWarningModal(message, index))}
            </div>
        );
    }

    function getChartButton() {
        function navigateToChartPage() {
            navigate("/chart");
        }

        if (user.role === "CLIENT") {
            return (
                <div className="flex justify-center">
                    <button
                        className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded mt-10"
                        onClick={navigateToChartPage}>
                        Show chart
                    </button>
                </div>
            );
        }
    }


    function getChat() {
        if (user.role === "CLIENT") {
            return <>
                <div className="fixed bottom-0 right-0 m-4 w-1/3 z-10">
                    <SupportChat/>
                </div>
            </>;
        } else if (user.role === "ADMIN") {
            return <div className="fixed bottom-0 right-0 m-4 w-full z-10">
                <AdminChatList activeChats={chatList}/>
            </div>;
        }
    }

    return (
        <div className="flex h-screen">
            <SockJsClient
                url={SOCKET_URL}
                topics={['/topic/notification/' + user.username]}
                onConnect={onConnected}
                onDisconnect={onDisconnected}
                onMessage={msg => onMessageReceived(msg)}
                debug={false}
            />
            {getModal()}
            {getWarningModals()}
            {/*<!-- Sidebar Section -->*/}
            {getAdminSideNav()}

            {/*<!-- Content Section -->*/}
            <div className="flex-1 pb-4 overflow-y-scroll">
                {/*<!-- Top Nav -->*/}
                <div className="flex items-center justify-between mb-6 border-b border-gray-400 px-5" style={{
                    height: "6%"
                }}>
                    <div>Hello, {user.username}</div>

                    <div className={areAllModalsClosed() ? "relative w-1over9 bg-gray-100 rounded" :
                        "w-1over9"}>
                        <div
                            className="flex flex-row w-full justify-between items-center py-2 rounded hover:cursor-pointer hover:bg-gray-200"
                            onClick={toggleOptionsDropdown}>
                            <div className="font-semibold items-center mx-2">
                                Options
                            </div>
                            {!isOptionsDropdownOpen ?
                                <BsChevronDown height="25" width="25" className="flex justify-center mx-2"/> :
                                <BsChevronUp height="25" width="25" className="flex justify-center mx-2"/>}
                        </div>
                        {isOptionsDropdownOpen && (
                            <div className="absolute w-full bg-gray-100 rounded">
                                <Link to="/"
                                      className="flex justify-start block w-full px-2 py-2 rounded hover:bg-gray-200"
                                      onClick={handleLogout}>Logout</Link>
                                <button onClick={toggleDeleteAccountModal}
                                        className="flex justify-start block w-full px-2 py-2 rounded hover:bg-gray-200">
                                    Delete Account
                                </button>
                            </div>
                        )}
                    </div>
                </div>

                {/*<!-- Profile Title -->*/}
                <h1 className="text-4xl font-semibold mb-6 text-center">Profile</h1>

                {/*<!-- Table -->*/}
                <div className="w-full">
                    {devices.length > 0 ?
                        getTableWithDevices() :
                        <p className="text-center text-2xl">You have no devices</p>}
                </div>
                {getUsersTable()}
                {getChartButton()}

                {/* SupportChat */}
                {getChat()}
            </div>
        </div>
    )
}