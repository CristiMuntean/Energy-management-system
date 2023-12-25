import {BrowserRouter, Route, Routes} from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Profile from "./pages/Profile";
import Chart from "./pages/Chart";

function Router() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/signup" element={<Register/>}/>
                <Route path="/profile" element={<Profile/>}/>
                <Route path="/chart" element={<Chart/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default Router;