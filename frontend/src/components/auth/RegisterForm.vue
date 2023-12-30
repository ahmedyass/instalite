<template>
    <h1>Register</h1>
    <form>
        <label for="username">Username</label>
        <input type="text" id="username" v-model="username" />
        <label for="password">Password</label>
        <input type="password" id="password" v-model="password" />
        <label for="email">Email</label>
        <input type="email" id="email" v-model="email" />
        <button type="button" @click="register">Register</button>
    </form>
</template>

<script setup>
import { ref } from 'vue';

const username = ref('');
const password = ref('');
const email = ref('');

const register = () => {
    fetch('http://spring-app:8080/api/v1/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username,
            password,
            email
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
