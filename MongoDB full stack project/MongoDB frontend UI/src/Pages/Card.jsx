import React from 'react'
import "./card.css";

export const Card = ({jobName,experience,skills,description}) => {
  return (
    <div className='card' >
       <h2>{jobName}</h2>

    <div className="details">
        <p className='description'>{description}</p>
        <p className='experience'>{experience}</p>
        <p className='skills'>{skills.map((skill) => ` ${skill}` )}</p>
    </div>


    </div>
  )
}
