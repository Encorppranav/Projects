import React, { useState } from 'react'
import "./JobApply.css";
import { Card } from './card';
import { useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router';

export const JobApply = () => {

  const navigateHome = useNavigate()

  const [query, setQuery] = useState('');
  const [post, setPost] = useState([]);

  useEffect(() => {


    const getPosts = async () => {
      const response = await axios.get(`http://localhost:8080/posts/${query}`)
      
     setPost(response.data);

    }
      



    const getAllPosts = async () => {
      const response = await axios.get("http://localhost:8080/allPosts")
       setPost(response.data);
       console.log(response.data)

      }

      


    if (query.length === 0) getAllPosts();

    if (query.length > 2) getPosts();


  }, [query])




  return (



    

    <div className="mainContainer">



      <div className="searchContainer">
        <input type='text' placeholder='Seacrh for job'

          onChange={(e) => setQuery(e.target.value)}
          />

          <span><button id = "homeBtn" onClick={(e) => navigateHome("/") } >Home </button> </span>


      </div>

      { post && (

      

      <div className="jobCards">
       {
        post.map((p,index) => 
        {
          return (
            <Card 
            jobName = {p.profile} 
            description = {p.desc} 
            skills = {p.techs} 
            experience = {p.exp}
            key = {index}
            />
          )
          
        }
      )
       }
      </div>

      )
      }

    </div>
    
  )
}
