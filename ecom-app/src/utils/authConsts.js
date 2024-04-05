export const IDENTITY_CONFIG = {
    authority: process.env.REACT_APP_AUTH_URL, //(string): The URL of the OIDC provider.
    client_id: process.env.REACT_APP_IDENTITY_CLIENT_ID, //(string): Your client application's identifier as registered with the OIDC provider.
    // client_secret: "blaaah",
    disablePKCE: false,
    redirect_uri: process.env.REACT_APP_REDIRECT_URL, //The URI of your client application to receive a response from the OIDC provider.
    login: process.env.REACT_APP_AUTH_URL + "/login",
    automaticSilentRenew: false, //(boolean, default: false): Flag to indicate if there should be an automatic attempt to renew the access token prior to its expiration.
    loadUserInfo: false, //(boolean, default: true): Flag to control if additional identity data is loaded from the user info endpoint in order to populate the user's profile.
    silent_redirect_uri: process.env.REACT_APP_SILENT_REDIRECT_URL, //(string): The URL for the page containing the code handling the silent renew.
    post_logout_redirect_uri: process.env.REACT_APP_LOGOFF_REDIRECT_URL, // (string): The OIDC post-logout redirect URI.
    response_type: "code", //(string, default: 'id_token'): The type of response desired from the OIDC provider.
    grantType: "authorization_code",
    scope: "openid", //(string, default: 'openid'): The scope being requested from the OIDC provider.
    webAuthResponseType: "token",
    monitorSession: true
};

export const METADATA_OIDC = {
    issuer: process.env.REACT_APP_AUTH_URL,
    authorization_endpoint: process.env.REACT_APP_AUTH_URL + "/oauth2/authorize",
    token_endpoint: process.env.REACT_APP_AUTH_URL + "/oauth2/token",
    userinfo_endpoint: process.env.REACT_APP_AUTH_URL + "/userinfo",
    end_session_endpoint: process.env.REACT_APP_AUTH_URL + "/connect/logout",
    revocation_endpoint: process.env.REACT_APP_AUTH_URL + "/oauth2/revoke"
};