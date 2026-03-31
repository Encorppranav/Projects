import React from 'react'
import "./Dashboard.css"
import { Form } from './Form.jsx'
import  image from "../images/adobeImage.png"
import { useNavigate } from 'react-router'

export const Dashboard = () => {

  const navigateHome = useNavigate();

  return (
  
    <div className='formPage'>

        <div className="header">
          
          <span><button id = "homeBtn"  onClick={(e) => navigateHome("/") } >Home </button> </span>
           
          
            <h1>Form Dashboard</h1>

         </div>
            
            
            <div className="content">
               <div className="sideImage">
                <img src= {image} alt="image1" />
               </div>

               <div className="formContent">
                <Form/>
                </div> 


           

        </div>




    </div>
  )
}
