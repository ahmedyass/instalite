<template>
    <h1>Logout</h1>
    <button @click="logout">Logout</button>
</template>

<script setup>
const logout = () => {
    fetch('http://spring-app:8080/api/v1/logout')
    .then(response => response.json())
    .then(data => {
        console.log('data:', data);
        localStorage.removeItem('token');
        window.location.href = '/';
    })
};
</script>
