<template>
    <h1>Login</h1>
    <form>
        <label for="username">Username</label>
        <input type="text" id="username" v-model="username" />
        <label for="password">Password</label>
        <input type="password" id="password" v-model="password" />
        <button type="button" @click="login">Login</button>
    </form>
</template>

<script setup>
import { ref } from 'vue';

const username = ref('');
const password = ref('');

const login = () => {
    fetch('http://spring-app:8080/api/v1/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username,
            password
        })
    })
    .then(response => response.json())
    .then(data => {
        console.log('data:', data);
        localStorage.setItem('token', data.token);
        window.location.href = '/';
    })
};
</script>
