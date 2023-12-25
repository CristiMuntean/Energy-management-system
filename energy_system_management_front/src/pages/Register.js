import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import {API_URLS} from "../utils/API_URLS";

export default function Register() {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
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
            "password": password
        }
        const role = "client";
        axios.post(API_URLS.AUTH_API_URL + 'register', {
            username,
            password,
            role: "CLIENT"
        }).then(response => {
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
                axios.get(API_URLS.DEVICE_API_URL + 'get_user_devices', {
                    headers: {
                        Authorization: "Bearer " + JSON.parse(localStorage.getItem("accessToken")).accessToken,
                        "userId": JSON.parse(localStorage.getItem("user")).id,
                    }
                }).then(response => {
                    console.log(response);
                    const devices = response.data;
                    localStorage.setItem("devices", JSON.stringify(devices));
                }).then(() => {
                    navigate("/profile");
                })
            }).catch(error => {
                console.log(error);
            });
        })
    }

    return (
        <div className="flex items-center flex-col justify-center h-screen p-10 -mt-20">
            <h1 className="text-center text-4xl font-bold pb-5">Register</h1>
            <form className="flex flex-col gap-y-2 text-left w-1/6">
                <label htmlFor="username">Username</label>
                <input type="text" id="username" name="username" className='border-black border rounded-md'
                       onChange={(e) => setUsername(e.target.value)}
                       value={username}/>
                <label htmlFor="password">Password</label>
                <input type="password" id="password" name="password" className="border-black border rounded-md"
                       onChange={(e) => setPassword(e.target.value)}
                       value={password}/>
                <button className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold my-3 py-2 px-4 rounded"
                        onClick={handleSubmit}>Register
                </button>
            </form>
        </div>
    )
}