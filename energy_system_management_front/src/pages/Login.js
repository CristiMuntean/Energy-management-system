import {useEffect, useState} from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import {API_URLS} from "../utils/API_URLS";

export default function Login() {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [errorMessage, setErrorMessage] = useState('')
    const navigate = useNavigate();

    useEffect(() => {
        if (localStorage.getItem("accessToken")) {
            navigate("/profile", {replace: true});
        }
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();

        const user = {
            "username": username,
            "password": password,
            "role": "client"
        }

        axios.post(API_URLS.AUTH_API_URL + 'login', {
            username,
            password
        })
            .then(response => {
                    console.log(response);
                    localStorage.setItem("accessToken", JSON.stringify(response.data));

                    axios.get(API_URLS.USER_API_URL + 'current_user', {
                        headers: {
                            Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken
                        }
                    }).then(response => {
                        console.log(response);
                        const user = response.data;
                        localStorage.setItem("user", JSON.stringify(user));
                    }).then(() => {
                        if (JSON.parse(localStorage.getItem("user")).role === "CLIENT") {
                            axios.get(API_URLS.DEVICE_API_URL + 'get_user_devices', {
                                headers: {
                                    "userId": JSON.parse(localStorage.getItem("user")).id,
                                    Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken
                                }
                            }).then(response => {
                                console.log(response);
                                const devices = response.data;
                                localStorage.setItem("devices", JSON.stringify(devices));
                            }).then(() => {
                                navigate("/profile");
                            })
                        } else {
                            axios.get(API_URLS.DEVICE_API_URL + 'get_all_devices_with_owning_user', {
                                headers: {
                                    Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken
                                }
                            })
                                .then(response => {
                                    console.log(response);
                                    const devices = response.data;
                                    localStorage.setItem("devices", JSON.stringify(devices));
                                })
                                .then(() => {
                                    const token = JSON.parse(localStorage.getItem("accessToken"));
                                    axios.get(API_URLS.USER_API_URL + 'get_all_users', {
                                        headers: {
                                            Authorization: "Bearer " + token.accessToken
                                        }
                                    }).then(response => {
                                        const users = response.data;
                                        localStorage.setItem("users", JSON.stringify(users));
                                        navigate("/profile");
                                    });
                                });
                        }
                    }).catch(error => {
                        console.log(error);
                    });

                }
            ).catch(error => {
            console.log("Login error:" + error);
            setErrorMessage("Invalid username or password");
        })
    }

    return (
        <div className="flex items-center flex-col gap-y-10 mx-auto h-screen justify-center -mt-20">
            <h1 className="text-4xl font-bold text-center">Login</h1>
            <form className="flex flex-col gap-y-2 w-1/6">
                <label htmlFor="username">Username</label>
                <input type="text" id="username" name="username" className='border-black border rounded-md'
                       onChange={(e) => setUsername(e.target.value)}
                       value={username}
                />

                <label htmlFor="password">Password</label>
                <input type="password" id="password" name="password" className="border-black border rounded-md"
                       onChange={(e) => setPassword(e.target.value)}
                       value={password}
                />
                <p className="text-red-500 text-center">{errorMessage}</p>
                <button
                    className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded text-center"
                    onClick={handleSubmit}>
                    Login
                </button>
            </form>
        </div>
    );
}