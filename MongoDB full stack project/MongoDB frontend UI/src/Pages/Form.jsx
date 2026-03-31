import React, { useState } from 'react'
import "./Form.css"
import {  useNavigate } from 'react-router-dom'
import axios from 'axios';
export const Form = () => {

    const navigate =  useNavigate();

    const initial = {profile:"",
                      exp:0,
                      desc:"",
                      techs:[]  
                    }

     
      const skillSet = [{name:"Java"},{name:"Python"},{name:"C/C++"},{name:"React/Angular"},{name:"SpringBoot/Django"}]   
      
    //   console.log(skillSet)



    const [formData,setFormData] = useState(initial)


    const HandleSubmit = async()=>
    {
        console.log(formData)

       await axios.post("http://localhost:8080/post",formData)
        .then((response) =>{
            console.log("Success")
            navigate("/apply")
        }
        ).catch((error) => console.log("Error",error))
    }


    const  handleChange = (value) =>{
        
        setFormData({...formData , techs :[...formData.techs,value]})

    }
    
    return (
        <div className='formDetails'>
            <span><h2>Create Job Post</h2></span>

            <div className="inputContainer">

         
            <div className="jobProfile">
                <label >Job Profile</label>
                <input type='text' placeholder='Job Profile'
                 onChange={(e) => setFormData({ ...formData,profile:e.target.value } ) } 
                 />
            </div>
            <div className="exp">
                <label >Experience</label>
                <input type='text' placeholder='Experience(Years)'
                    onChange={(e) => setFormData({ ...formData,exp:e.target.value } ) } 
                />
            </div>

            <div className="desc">
                <label >Decription</label>
                <input type='text' placeholder='Description'
                    onChange={(e) => setFormData({ ...formData,desc:e.target.value } ) } 
                />
            </div>

               </div>

               <div className="skillContainer">

                <h2>Select required skills</h2>

                <div className="checkboxes">

            {
                skillSet.map((item,index) =>
                {

                    return(

                        <div className= {item.name} key={index}>
                <input type='checkbox' value={item.name} onChange={(e) => handleChange(e.target.value)} />
                 <label >{item.name}</label>
                </div>
                )


                } )
            }
              

            

              </div>

               </div>

               <div className="submitBtn" onClick={HandleSubmit}>Submit</div>

        </div>
    )
}