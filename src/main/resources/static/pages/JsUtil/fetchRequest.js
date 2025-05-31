function getCsrfToken() {
  const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const headerName = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  if (token && headerName) {
    return {
      [headerName]: token
    };
  }
  return {};
}

function sendFetchRequest(method, url, body) {
  console.log("url :::::::" + url);
  return fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json',
      ...getCsrfToken()
    },
    body: JSON.stringify(body),
  })
  .then(response => {
    console.log("response ::");
    console.log(response);
    if (response.status === 200) {
      successAlert();
    }else{
      response.json().then(error => {
        errorAlert(error.message)});
    }
  })
  .catch(error => {
    errorAlert(error.message);
  });
}


function successAlert(){

  Swal.fire({
    title: "success",
    icon: "success",
    draggable: true
  });

}

function errorAlert(message){
  Swal.fire({
    icon: "error",
    title: "Oops...",
    text: message,
  });
}