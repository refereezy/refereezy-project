/**
 * base.js - Common utility functions for Refereezy
 * This file contains shared functionality used across all pages
 */

// Global API URL - centralized configuration
// This URL should be updated to match the backend server's address: http://refereezy.smcardona.tech:8080
const API_URL = "http://localhost:8888";

/**
 * Shows a toast notification
 * @param {string} message - The message to display
 * @param {string} type - Notification type: 'info', 'success', 'error', 'warning'
 */
async function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    const p = document.createElement('p');
    
    // Handle newlines by replacing them with <br> elements
    if (message.includes('\n')) {
        p.innerHTML = message.split('\n').join('<br>');
    } else {
        p.innerHTML = message;
    }
    
    notification.className = `notification ${type}`;
    notification.appendChild(p);
    
    document.body.appendChild(notification);
    
    notification.classList.add('show');
    
    await new Promise(resolve => setTimeout(resolve, 3000));
    notification.classList.remove('show');
    
    await new Promise(resolve => setTimeout(resolve, 300));
    document.body.removeChild(notification);
    
}

/**
 * Logout function - clears local storage and redirects to login page
 */
function logout() {
    localStorage.removeItem('client_id');
    localStorage.removeItem('user_type');
    localStorage.removeItem('token');
    window.location.href = './login.html';
}

/**
 * Get logged in client ID
 * @returns {string|null} The client ID or null if not logged in
 */
function getClientId() {
    return localStorage.getItem('client_id');
}

/**
 * Format a date as a locale string
 * @param {string|Date} dateStr - Date string or Date object
 * @returns {string} Formatted date string
 */
function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString();
}

/**
 * Format time from a date as HH:MM
 * @param {string|Date} dateStr - Date string or Date object
 * @returns {string} Formatted time string
 */
function formatTime(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

/**
 * Check if the user is logged in, redirect to login if not
 */
function checkAuth() {
    const clientId = getClientId();
    if (!clientId) {
        window.location.href = './login.html';
    }
}