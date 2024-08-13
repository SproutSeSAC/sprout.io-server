window.onload = function () {
    google.accounts.id.initialize({
        client_id: '40136960646-837hal599d9v41gj47ode59qs28tf3ti.apps.googleusercontent.com',
        callback: handleCredentialResponse,
        auto_select: true  // 이전에 로그인한 기록이 있다면 자동 로그인 시도
    });
    //google.accounts.id.prompt(); // 자동 로그인 프로세스 시작
};

function handleCredentialResponse(response) {
    console.log("Encoded JWT ID token: " + response.credential);
    // 서버로 JWT ID 토큰을 보내어 인증 처리
    window.location.href = '/oauth2/authorization/google'//"/login/oauth2/code/google?credential=" + response.credential;
    //"/oauth2/authorization/google"//"/login/oauth2/code/google?credential=" + response.credential;
}
