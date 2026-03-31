import React from 'react'
import "./Home.css"
import { Link } from 'react-router-dom'

export const Home = () => {
  return (
    <>
    <div className="homecontainer">

    <header>

    <h1>Get Hired or Hire People for free !</h1>
    </header>


      <div className="buttons">

      <Link to = {"/hire"}>
      <button  className='hiringbtn'> Hire Talent</button>
       </Link>
     
     <Link to={"/apply"}>
      <button className='applybtn'>Get Jobs now</button>
     </Link>

      </div>

    
      


    </div>
    </>
  )
}
