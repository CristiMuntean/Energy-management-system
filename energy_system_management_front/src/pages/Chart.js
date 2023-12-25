import {useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {BsChevronDown, BsChevronUp} from "react-icons/bs";
import axios from "axios";
import {API_URLS} from "../utils/API_URLS";
import JsCharting from "jscharting-react";
import SockJsClient from "react-stomp";
import SupportChat from "./SupportChat";

export default function Chart() {
    const SOCKET_URL = 'http://localhost:8083/ws-monitoring'
    const navigate = useNavigate();
    const [user, setUser] = useState({
        username: "",
        password: "",
        role: ""
    });
    const [devices, setDevices] = useState([]);
    const [isOptionsDropdownOpen, setIsOptionsDropdownOpen] = useState(false);
    const [isDeleteAccountModalOpen, setIsDeleteAccountModalOpen] = useState(false);
    const [chartOptions, setChartOptions] = useState({
        type: 'line spline',
        height: 500,
        xAxis_label_text: 'Hour',
        // yAxis_label_text: 'Energy value [kWh]',
        yAxis: {
            title: 'Energy value [kWh]'
        },
        series: [
            {
                points: [
                    {x: '00', y: null},
                    {x: '01', y: null},
                    {x: '02', y: null},
                    {x: '03', y: null},
                    {x: '04', y: null},
                    {x: '05', y: null},
                    {x: '06', y: null},
                    {x: '07', y: null},
                    {x: '08', y: null},
                    {x: '09', y: null},
                    {x: '10', y: null},
                    {x: '11', y: null},
                    {x: '12', y: null},
                    {x: '13', y: null},
                    {x: '14', y: null},
                    {x: '15', y: null},
                    {x: '16', y: null},
                    {x: '17', y: null},
                    {x: '18', y: null},
                    {x: '19', y: null},
                    {x: '20', y: null},
                    {x: '21', y: null},
                    {x: '22', y: null},
                    {x: '23', y: null},
                ]
            }
        ]
    });
    const [chosenDate, setChosenDate] = useState("");

    const onConnected = () => {
        console.log('Connected!!!');
    }

    const onMessageReceived = (msg) => {
        console.log('Message received: ', msg);
        const userDevices = [...devices];
        console.log(chosenDate);
        const dateObject = new Date(chosenDate);
        const day = dateObject.getDate();
        const month = dateObject.getMonth() + 1;
        const year = dateObject.getFullYear();
        if (msg.day !== day || msg.month !== month || msg.year !== year) return;

        const deviceIndex = userDevices.findIndex((device) => device.id === msg.id);

        if (deviceIndex !== -1) {
            const updatedSeries = chartOptions.series.map((seriesItem) => {
                if (seriesItem.name === userDevices[deviceIndex].description) {
                    // Update the modified series
                    return {
                        ...seriesItem,
                        points: seriesItem.points.map((point, index) => {
                            if (index === msg.hour) {
                                // Update the specific point in the modified series
                                return {x: point.x, y: msg.reading};
                            }
                            return point;
                        }),
                    };
                }
                // Keep other series unchanged
                return seriesItem;
            });

            setChartOptions({
                height: 500,
                xAxis_label_text: 'Hour',
                yAxis_label_text: 'Energy value [kWh]',
                series: updatedSeries,
            });
        }
    }

    const onDisconnected = () => {
        console.log('Disconnected!!!');
    }


    const toggleOptionsDropdown = () => {
        setIsOptionsDropdownOpen(!isOptionsDropdownOpen);
    }

    const handleLogout = () => {
        localStorage.clear();
        let allMessages = JSON.parse(localStorage.getItem("messages"));
        //keep only messages that are not from the current user
        if (allMessages) {
            allMessages = allMessages.filter((msg) => msg.sender !== user.username || msg.receiver !== user.username);
        }
        localStorage.clear();
        localStorage.setItem("messages", JSON.stringify(allMessages));
    }

    const toggleDeleteAccountModal = () => {
        if (isOptionsDropdownOpen) toggleOptionsDropdown();
        setIsDeleteAccountModalOpen(!isDeleteAccountModalOpen);
    }

    useEffect(() => {
        const tempUser = localStorage.getItem("user");
        if (!tempUser) {
            localStorage.clear();
            navigate("/");
        }
        setUser(JSON.parse(tempUser));
        console.log(user);

        const tempDevices = localStorage.getItem("devices");

        const tempDevicesArray = JSON.parse(tempDevices);
        setDevices(tempDevicesArray);
        console.log(devices);
        console.log(JSON.parse(tempDevices));

    }, []);


    function getModal() {

        function getDeleteAccountModal() {
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

            return (
                <div className="w-screen h-screen fixed bg-black/40 flex items-center">
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
                (isDeleteAccountModalOpen &&
                    getDeleteAccountModal()
                )
            }
            {/*modal end*/}
        </>;
    }


    function getChart() {

        function requestMeasuringData(date) {
            setChosenDate(date);
            console.log(chosenDate);
            console.log(devices);
            console.log(date);
            axios.get(API_URLS.MONITORING_API_URL + 'get_readings_for_day', {
                params: {
                    "date": date,
                    "deviceIds": devices.map(device => device.id).join(" ")
                },
                headers: {
                    Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken
                }
            }).then(response => {
                console.log(response);
                const deviceMeasuringDataArray = response.data.devices;
                const series = [];
                deviceMeasuringDataArray.forEach(deviceMeasuringData => {
                    series.push({
                        emptyPointMode: 'ignore',
                        name: devices.find(device => device.id === deviceMeasuringData.id).description,
                        points: deviceMeasuringData.readings.map(measuringData => {
                            return {
                                x: measuringData.hour.length === 1 ? "0" + measuringData.hour : measuringData.hour,
                                y: measuringData.reading
                            }
                        })
                    })
                })
                console.log(series);

                let allPointsNull = true;
                series.forEach(serie => {
                    serie.points.forEach(point => {
                        if (point.y !== null) {
                            allPointsNull = false;
                        } else {
                            point.y = 0;
                        }
                    })
                })
                setChartOptions({
                    height: 500,
                    xAxis_label_text: 'Hour',
                    yAxis_label_text: 'Energy value [kWh]',
                    series: series
                })
            })
        }

        return (
            <div>
                <div className="flex flex-col items-start">
                    {/*create a div with a date input*/}
                    <div className="flex flex-row ml-5">
                        <label htmlFor="date">Date: </label>
                        <input type="date" id="date" name="date" className='border-black border rounded-md mx-2'
                               onChange={(e) => requestMeasuringData(e.target.value)}
                        />
                    </div>
                </div>
                <div className="flex flex-col items-center">
                    <JsCharting options={chartOptions} mutable={true}/>
                </div>
            </div>

        );
    }

    return (
        <div className="flex-1 pb-4 overflow-y-scroll">
            <SockJsClient
                url={SOCKET_URL}
                topics={['/topic/reading/' + user.username]}
                onConnect={onConnected}
                onDisconnect={onDisconnected}
                onMessage={msg => onMessageReceived(msg)}
                debug={false}
            />
            {getModal()}
            {/*<!-- Top Nav -->*/}
            <div className="flex items-center justify-between mb-6 border-b border-gray-400 px-5" style={{
                height: "6%"
            }}>
                <div>
                    <Link to={"/profile"} className="hover:text-gray-400">Back To Profile</Link>
                </div>

                <div className="relative w-1over9 bg-gray-100 rounded">
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

            {/*<!-- Chart Title -->*/}
            <h1 className="text-4xl font-semibold mb-6 text-center">Chart</h1>

            {/*<!-- Chart -->*/}
            {getChart()}

            {/* SupportChat */}
            <div className="fixed bottom-0 right-0 m-4 w-1/4">
                <SupportChat/>
            </div>
        </div>
    )
}