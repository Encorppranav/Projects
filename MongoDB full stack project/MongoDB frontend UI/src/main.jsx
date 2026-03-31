import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import {createBrowserRouter, RouterProvider,} from "react-router-dom";
import { JobApply } from './Pages/JobApply.jsx';
import { Home } from './Pages/Home.jsx';
import { Dashboard } from './Pages/Dashboard.jsx';


const router = createBrowserRouter([
    {
        path: '/',
        element: <App />,
        

        children: [
            {
                index: true,
                element: <Home />

            },

           

            {
                path: '/apply',
                element: <JobApply />

            },

            {
                path:'hire',
                element:<Dashboard/>
            }


        ]

    },


])



const root = createRoot(document.querySelector('#root'))




root.render(

    <RouterProvider router={router} />

)

