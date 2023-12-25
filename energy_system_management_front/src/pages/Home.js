import {Link, useNavigate} from "react-router-dom";
import {useEffect} from "react";

export default function Home() {
    const navigate = useNavigate();

    useEffect(() => {
        try {
            const token = JSON.parse(localStorage.getItem("accessToken"));
            if (token) {
                navigate("/profile", {replace: true})
            }
        } catch (e) {
            console.log(e);
        }
    }, []);

    return (
        <div className="flex flex-col items-center justify-center h-screen w-full -mt-20">
            <h1 className="text-center text-4xl font-bold p-10">Energy Management System</h1>
            <div className="flex justify-center gap-x-5 items-center w-full">
                <Link to="login"
                      className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded text-center">
                    Login
                </Link>
                <Link to="signup"
                      className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded text-center">
                    Register
                </Link>
            </div>
        </div>
    );
}