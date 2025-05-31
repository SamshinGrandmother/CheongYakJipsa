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

function sendFetchRequest(method, url, body, afterSuccess) {
  return fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json',
      ...getCsrfToken()
    },
    body: JSON.stringify(body),
  }).then(async response => {

    const isJson = response.headers.get("Content-Type")?.includes("application/json");
    let responseJsonData = null;
    if(isJson) responseJsonData = await response.json();
    if (response.ok) {
      successAlert(responseJsonData?.message || "성공적으로 저장되었습니다.");
      if(afterSuccess) afterSuccess();

    }else{
      errorAlert(responseJsonData?.message || "저장에 실패하였습니다.");
    }
  })
  .catch(error => {
    errorAlert(error.message);
  });
}

function successAlert(successMessage){

  Swal.fire({
    title: successMessage,
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