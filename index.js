import { NativeModules, NativeEventEmitter, Platform } from "react-native";

// const EventEmitter = new NativeEventEmitter(ShareMenu);

const NEW_SHARE_EVENT_NAME = "NewShareEvent";
const ShareMenuReactView = Platform.OS == "ios" ? NativeModules.ShareMenuReactView : NativeModules.ShareMenu


export default {
  /**
   * @deprecated Use `getInitialShare` instead. This is here for backwards compatibility.
   */
  dismissExtension(error = null) {
    ShareMenuReactView.dismissExtension(error);
  },
  openApp() {
    ShareMenuReactView.openApp();
  },
  continueInApp(extraData = null) {
    ShareMenuReactView.continueInApp(extraData);
  },
  data() {
    return ShareMenuReactView.data();
  },
  // addNewShareListener(callback) {
  //   const subscription = EventEmitter.addListener(
  //     NEW_SHARE_EVENT_NAME,
  //     callback
  //   );

  //   return subscription;
  // },
};
