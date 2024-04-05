import Cookies from "js-cookie";
import { IDENTITY_CONFIG, METADATA_OIDC } from "../utils/authConsts";
import { UserManager, WebStorageStateStore, Log } from "oidc-client";

export default class AuthService {
    UserManager;

    constructor() {
        this.UserManager = new UserManager({
            ...IDENTITY_CONFIG,
            userStore: new WebStorageStateStore({ store: window.sessionStorage }),
            metadata: {
                ...METADATA_OIDC
            }
        });
        // Logger
        Log.logger = console;
        Log.level = Log.DEBUG;
        this.UserManager.events.addUserLoaded((user) => {
            if (window.location.href.indexOf("signin-oidc") !== -1) {
                this.navigateToHome();
            }
        });

        this.UserManager.events.addUserSignedOut((user) => {
            if (window.location.href.indexOf("logout") !== -1) {
                this.navigateToHome();
            }
        });
        this.UserManager.events.addSilentRenewError((e) => {
            console.log("silent renew error", e.message);
        });

        this.UserManager.events.addAccessTokenExpired(() => {
            console.log("token expired");
            this.signinSilent();
        });
    }

    signinRedirectCallback = () => {
        this.UserManager.signinRedirectCallback().then(() => {
            "";
        });
    };


    getUser = async () => {
        const user = await this.UserManager.getUser();
        if (!user) {
            return await this.UserManager.signinRedirectCallback();
        }
        return user;
    };

    parseJwt = (token) => {
        const base64Url = token.split(".")[1];
        const base64 = base64Url.replace("-", "+").replace("_", "/");
        return JSON.parse(window.atob(base64));
    };


    signinRedirect = () => {
        localStorage.setItem("redirectUri", window.location.pathname);
        this.UserManager.signinRedirect({});
    };


    navigateToHome = () => {
        window.location.replace("/");
    };


    isAuthenticated = () => {
        const oidcStorage = JSON.parse(sessionStorage.getItem(`oidc.user:${process.env.REACT_APP_AUTH_URL}:${process.env.REACT_APP_IDENTITY_CLIENT_ID}`))

        return (!!oidcStorage && !!oidcStorage.access_token)
    };

    getAccessToken = () => {
        const oidcStorage = JSON.parse(sessionStorage.getItem(`oidc.user:${process.env.REACT_APP_AUTH_URL}:${process.env.REACT_APP_IDENTITY_CLIENT_ID}`))
        if(!!oidcStorage && !!oidcStorage.access_token){
            return oidcStorage.access_token;
        }
        else{
            return ' ';
        }
    };

    getUsername = () => {
        const oidcStorage = JSON.parse(sessionStorage.getItem(`oidc.user:${process.env.REACT_APP_AUTH_URL}:${process.env.REACT_APP_IDENTITY_CLIENT_ID}`))
        if(!!oidcStorage && !!oidcStorage.profile  && !!oidcStorage.profile.sub){
            return oidcStorage.profile.sub;
        }
        else{
            return ' ';
        }
    };

    signinSilent = () => {
        this.UserManager.signinSilent()
            .then((user) => {
                console.log("signed in", user);
            })
            .catch((err) => {
                console.log(err);
            });
    };
    signinSilentCallback = () => {
        this.UserManager.signinSilentCallback();
    };

    createSigninRequest = () => {
        return this.UserManager.createSigninRequest();
    };

    logout = () => {
        console.log(this.UserManager.getUser());
        
        this.UserManager.signoutRedirect({
            id_token_hint: localStorage.getItem("id_token"),
        });
        this.UserManager.clearStaleState();
    };



    signoutRedirectCallback = () => {
        localStorage.clear();
        window.location.replace(process.env.REACT_APP_PUBLIC_URL);
        this.UserManager.signoutRedirectCallback().then(() => {
            localStorage.clear();
            window.location.replace(process.env.REACT_APP_PUBLIC_URL);
        });
        this.UserManager.clearStaleState();

    };
}