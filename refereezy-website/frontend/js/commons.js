const auth_ok = localStorage.getItem("auth_ok")
if (!auth_ok){
  window.location.href = "../../login.html";
}

function logout() {
  localStorage.removeItem("auth_ok");
  localStorage.removeItem("client_id");
  window.location.href = "../../index.html";
}

