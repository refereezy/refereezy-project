/**
 * base.js - Common utility functions for Refereezy
 * This file contains shared functionality used across all pages
 */

// Global API URL - centralized configuration
const API_URL = "http://localhost:8080";

/**
 * Shows a toast notification
 * @param {string} message - The message to display
 * @param {string} type - Notification type: 'info', 'success', 'error', 'warning'
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.classList.add('show');
        
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => {
                document.body.removeChild(notification);
            }, 300);
        }, 3000);
    }, 100);
}

/**
 * Logout function - clears local storage and redirects to login page
 */
function logout() {
    localStorage.removeItem('client_id');
    localStorage.removeItem('user_type');
    localStorage.removeItem('token');
    window.location.href = '/login.html';
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
    const clientId = localStorage.getItem('client_id');
    if (!clientId) {
        window.location.href = '/login.html';
    }
}

// Initialize common functionality on page load
document.addEventListener('DOMContentLoaded', () => {
    // Apply any common page setup here
    checkAuth();
});