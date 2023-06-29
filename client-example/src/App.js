
import './App.css';
import React, { useState } from 'react';

import { EventSourcePolyfill } from "event-source-polyfill";



function App() {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const [isConnected, setIsConnected] = useState(false);

    const [token, setToken] = useState('');
    const [username, setUsername] = useState('');


    const clientExample1 = () => {
        if (!isConnected) {
            if(!username){
                alert('접속자명 입력해주세요');
                return;
            }
            setMessage('');     // message 상태를 초기화
            setMessages([]);    // messages 상태를 초기화

            const eventSource = new EventSource(`http://localhost:8080/api/connect/${username}`, { withCredentials: true });

            eventSource.addEventListener('open',(event)=>{
                console.log("open : ",event);
            });
            eventSource.addEventListener('notification', (event) => {
                const newMessage = event.data;
                console.log( "json :: " ,JSON.parse(event.data));
                setMessage(newMessage);
                setMessages((prevMessages) => [newMessage, ...prevMessages]);  // 최신 데이터가 맨위로
            });


            eventSource.addEventListener('error', (error) => {
                console.log('SSE 오류가 발생했습니다.   :  ', error ,   "        status :",eventSource.readyState);
                console.log('SOURCE  : ' ,eventSource);
                eventSource.close();
                setIsConnected(false);
            });
            setIsConnected(true);
        }
    };

    const clientExample2 = () => {
        if (!isConnected) {
            if (!username) {
                alert('접속자명 입력해주세요');
                return;
            }
            if(!token){
                alert('토큰 입력해보세요~')
                return;
            }
            setMessage('');
            setMessages([]);

            const url = `http://localhost:8080/connect/${username}`;
            const eventSource = new EventSourcePolyfill(url,  {
                headers: {
                    Authorization: `Bearer ${token}`
                },
                heartbeatTimeout: 60*1000
            });
            eventSource.onopen = (event) => {
                console.log("open : " ,event);
            }

            eventSource.addEventListener('notification', (event) => {
                console.log("noti : " , event);
                const newMessage = event.data;
                console.log( "json :: " ,JSON.parse(event.data));
                setMessage(newMessage);
                setMessages((prevMessages) => [newMessage, ...prevMessages]);  // 최신 데이터가 맨위로
            });

            eventSource.onerror = (e)=>{
                console.log("onerror : " , e);
            }

            eventSource.addEventListener('error', (event) => {
                // console.error('SSE 오류가 발생했습니다.', event);
                console.log(' Error : ' , event);
                console.log(" e : ", eventSource);
                eventSource.close();
                setIsConnected(false);
            });
            setIsConnected(true);
        }
    };





    const usernameChange = (event) => {
        setUsername(event.target.value);
    };
    const tokenChange = (e) =>{
        setToken(e.target.value);
    };
  return (
    <div className="App">
        <div>
            <input type="text" value={username} onChange={usernameChange} placeholder="접속자 이름"/>
            <button onClick={clientExample1}>
                {isConnected ? '연결 중(F : Example_01)' : '연결 시작(F : Example_01)'}
            </button>
        </div>
        <div>
            <input type="text" value={token} onChange={tokenChange} placeholder="토큰 넣을때 "/>
            <button onClick={clientExample2}>
                {isConnected ? '연결 중(F : Example_02)' : '연결 시작(F : Example_02)'}
            </button>
        </div>
        <p>서버로부터의 메시지 마지막 메세지  :  {message} </p>
        <p>{message.notification}</p>


        <div id="row">
            {messages.map((message, index) => (
                <p key={index}>{message}</p>
            ))}
        </div>

    </div>
  );
}

export default App;
