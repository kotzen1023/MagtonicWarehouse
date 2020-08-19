package com.magtonic.magtonicwarehouse.data

class Constants {
    class BluetoothState {
        companion object {
            const val MESSAGE_STATE_CHANGE = 1
            const val MESSAGE_READ = 2
            const val MESSAGE_WRITE = 3
            const val MESSAGE_DEVICE_NAME = 4
            const val MESSAGE_TOAST = 5
        }
    }



    class ACTION {
        companion object {
            //const val ACTION_CHECK_EXIST : String = "com.magtonic.MagtonicWarehoouse.CheckExistAction"
            const val ACTION_LOGIN_ACTION : String = "com.magtonic.MagtonicWarehoouse.LoginAction"
            const val ACTION_LOGIN_SUCCESS : String = "com.magtonic.MagtonicWarehoouse.LoginSuccess"
            const val ACTION_LOGIN_FAILED : String = "com.magtonic.MagtonicWarehoouse.LoginFailed"
            const val ACTION_LOGIN_NETWORK_ERROR : String = "com.magtonic.MagtonicWarehoouse.LoginNetworkError"

            const val ACTION_LOGOUT_ACTION : String = "com.magtonic.MagtonicWarehoouse.LogoutAction"
            const val ACTION_HIDE_KEYBOARD : String = "com.magtonic.MagtonicWarehoouse.HideKeyboardAction"
            const val ACTION_CONNECTION_TIMEOUT : String = "com.magtonic.MagtonicWarehoouse.ConnectionTimeOut"
            const val ACTION_SERVER_ERROR : String = "com.magtonic.MagtonicWarehoouse.ServerError"
            //const val ACTION_SHOW_KEYBOARD : String = "com.magtonic.MagtonicWarehoouse.ShowKeyboardAction"
            const val ACTION_LOGIN_FRAGMENT_LOGIN_FAILED : String = "com.magtonic.MagtonicWarehoouse.LoginFragmentLoginFailedAction"
            const val ACTION_NETWORK_FAILED : String = "com.magtonic.MagtonicWarehoouse.ActionNetworkFailed"
            const val ACTION_WIFI_STATE_CHANGED : String = "com.magtonic.MagtonicWarehoouse.ActionWifiStateChanged"
            const val ACTION_USER_INPUT_SEARCH : String = "com.magtonic.MagtonicWarehoouse.UserInputSearch"
            const val ACTION_BARCODE_NULL : String = "com.magtonic.MagtonicWarehoouse.BarcodeNull"
            //Soap
            //const val ACTION_SOCKET_TIMEOUT : String = "com.magtonic.MagtonicWarehoouse.SocketTimeOut"
            //const val ACTION_SOAP_CONNECTION_FAIL : String = "com.magtonic.MagtonicWarehoouse.SoapConnectionFail"

            //receipt
            const val ACTION_RECEIPT_FRAGMENT_REFRESH : String ="com.magtonic.MagtonicWarehoouse.ReceiptFragmentRefresh"

            const val ACTION_RECEIPT_NO_NOT_EXIST : String = "com.magtonic.MagtonicWarehoouse.ReceiptNoNotExist"
            const val ACTION_RECEIPT_SCAN_BARCODE : String = "com.magtonic.MagtonicWarehoouse.ReceiptScanBarcode"
            const val ACTION_RECEIPT_MODIFY_CHANGED : String = "com.magtonic.MagtonicWarehoouse.ReceiptModifyChanged"
            const val ACTION_RECEIPT_MODIFY_NO_CHANGED : String = "com.magtonic.MagtonicWarehoouse.ReceiptModifyNoChanged"
            const val ACTION_RECEIPT_UPLOAD_ACTION: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadAction"
            const val ACTION_RECEIPT_UPLOAD_SUCCESS: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadSuccess"
            const val ACTION_RECEIPT_UPLOAD_FAILED: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadFailed"
            const val ACTION_RECEIPT_UPLOADED_NO: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadedNo"
            const val ACTION_RECEIPT_UPLOADED_YES: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadedYes"
            const val ACTION_RECEIPT_UPLOADED_SEND_TO_FRAGMENT: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadedSendToFragment"
            const val ACTION_RECEIPT_UPLOAD_FAILED_SEND_TO_FRAGMENT: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadFailedSendToFragment"
            const val ACTION_RECEIPT_UPLOAD_RETURN_PO_DIFF: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadReturnPoDiff"
            const val ACTION_RECEIPT_UPLOAD_RETURN_EXCEPTION: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadReturnException"
            const val ACTION_RECEIPT_UPLOADED_CONFIRM_ACTION: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadedConfirmAction"
            const val ACTION_RECEIPT_UPLOADED_CONFIRM_SUCCESS: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadedConfirmSuccess"
            const val ACTION_RECEIPT_UPLOADED_CONFIRM_FAILED: String = "com.magtonic.MagtonicWarehoouse.ReceiptUploadedConfirmFailed"
            //20200601
            const val ACTION_RECEIPT_SHOW_CONFIRM_FAILED_HISTORY : String = "com.magtonic.MagtonicWarehoouse.ReceiptShowConfirmFailedHistory"
            const val ACTION_RECEIPT_SHOW_CONFIRM_FAILED_MORE_DETAIL : String = "com.magtonic.MagtonicWarehoouse.ReceiptShowConfirmFailedMoreDetail"
            const val ACTION_RECEIPT_SHOW_CONFIRM_FAILED_BACK_TO_HISTORY_LIST : String = "com.magtonic.MagtonicWarehoouse.ReceiptShowConfirmFailedBackToHistoryList"

            //storage
            const val ACTION_STORAGE_SCAN_BARCODE : String = "com.magtonic.MagtonicWarehoouse.StorageScanBarcode"
            const val ACTION_STORAGE_FRAGMENT_REFRESH : String ="com.magtonic.MagtonicWarehoouse.StorageFragmentRefresh"
            const val ACTION_STORAGE_MODIFY_CHANGED : String = "com.magtonic.MagtonicWarehoouse.StorageModifyChanged"
            const val ACTION_STORAGE_MODIFY_NO_CHANGED : String = "com.magtonic.MagtonicWarehoouse.StorageModifyNoChanged"
            const val ACTION_STORAGE_RECEIPT_NO_NOT_EXIST : String = "com.magtonic.MagtonicWarehoouse.StorageReceiptNoNotExist"
            const val ACTION_STORAGE_UPLOADED_CANNOT_LOAD : String = "com.magtonic.MagtonicWarehoouse.StorageUploadedCannotLoad"
            const val ACTION_STORAGE_UPDATE_ACTION : String = "com.magtonic.MagtonicWarehoouse.StorageUpdateAction"
            const val ACTION_STORAGE_UPLOAD_SUCCESS: String = "com.magtonic.MagtonicWarehoouse.StorageUploadSuccess"
            const val ACTION_STORAGE_UPLOAD_FAILED: String = "com.magtonic.MagtonicWarehoouse.StorageUploadFailed"
            //const val ACTION_STORAGE_UPLOAD_CONFIRM: String = "com.magtonic.MagtonicWarehoouse.StorageUploadConfirm"
            //const val ACTION_STORAGE_UPLOAD_CANCEL: String = "com.magtonic.MagtonicWarehoouse.StorageUploadCancel"
            const val ACTION_STORAGE_UPLOAD_RETURN_EXCEPTION: String = "com.magtonic.MagtonicWarehoouse.StorageUploadReturnException"
            //material send
            const val ACTION_MATERIAL_SCAN_BARCODE : String = "com.magtonic.MagtonicWarehoouse.MaterialScanBarcode"
            const val ACTION_MATERIAL_MODIFY_CHANGED : String = "com.magtonic.MagtonicWarehoouse.MaterialModifyChanged"
            const val ACTION_MATERIAL_MODIFY_NO_CHANGED : String = "com.magtonic.MagtonicWarehoouse.MaterialModifyNoChanged"
            const val ACTION_MATERIAL_NO_NOT_EXIST : String = "com.magtonic.MagtonicWarehoouse.MaterialNoNotExist"
            const val ACTION_MATERIAL_FRAGMENT_REFRESH : String ="com.magtonic.MagtonicWarehoouse.MaterialFragmentRefresh"
            const val ACTION_MATERIAL_SEND_CHANGED_UPDATE_ACTION : String = "com.magtonic.MagtonicWarehoouse.MaterialSendChangedUpdateAction"
            const val ACTION_MATERIAL_SEND_CHANGED_ERROR : String = "com.magtonic.MagtonicWarehoouse.MaterialSendChangedError"
            const val ACTION_MATERIAL_SEND_CHANGED_SUCCESS : String = "com.magtonic.MagtonicWarehoouse.MaterialSendChangedSuccess"
            const val ACTION_MATERIAL_SEND_CHANGED_FAILED : String = "com.magtonic.MagtonicWarehoouse.MaterialSendChangedFailed"
            const val ACTION_MATERIAL_SEND_CHANGED_COMPLETE : String = "com.magtonic.MagtonicWarehoouse.MaterialSendChangedComplete"
            const val ACTION_MATERIAL_SEND_COMPLETE_TO_FRAGMENT: String = "com.magtonic.MagtonicWarehoouse.MaterialSendCompleteSendToFragment"
            const val ACTION_MATERIAL_REAL_SEND_CAN_NOT_MUCH_MORE_THAN_STORAGE: String = "com.magtonic.MagtonicWarehoouse.MaterialRealSendCanNotMuchMoreThanS"
            const val ACTION_MATERIAL_QUANTITY_MUST_BE_INTEGER: String = "com.magtonic.MagtonicWarehoouse.MaterialQuantityMustBeInteger"
            const val ACTION_MATERIAL_QUANTITY_IN_STOCK_EMPTY: String = "com.magtonic.MagtonicWarehoouse.MaterialQuantityInStockEmpty"
            const val ACTION_SEEK_BAR_SELECT_PAGE_ACTION: String = "com.magtonic.MagtonicWarehoouse.SeekbarSelectPageAction"
            const val ACTION_SEEK_BAR_SHOW_ACTION: String = "com.magtonic.MagtonicWarehoouse.SeekbarShowAction"
            const val ACTION_SEEK_BAR_HIDE_ACTION: String = "com.magtonic.MagtonicWarehoouse.SeekbarHideAction"
            //setting
            const val ACTION_SETTING_PRINTTEST_ACTION: String = "com.magtonic.MagtonicWarehoouse.SettingPrintTest"
            const val ACTION_SETTING_BLUETOOTH_CONNECT_ACTION: String = "com.magtonic.MagtonicWarehoouse.SettingBluetoothConnectAction"
            const val ACTION_SETTING_BLUETOOTH_STATE_CHANGE: String = "com.magtonic.MagtonicWarehoouse.SettingBluetoothStateChange"
            //home
            const val ACTION_HOME_GO_TO_RECEIPT_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToReceiptAction"
            const val ACTION_HOME_GO_TO_STORAGE_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToStorageAction"
            const val ACTION_HOME_GO_TO_MATERIAL_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToMaterialAction"
            const val ACTION_HOME_GO_TO_ISSUANCE_LOOKUP_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToIssuanceLookupAction"
            const val ACTION_HOME_GO_TO_TAG_PRINTER_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToTagPrinterAction"
            const val ACTION_HOME_GO_TO_LOGOUT_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToLogoutAction"
            const val ACTION_HOME_GO_TO_ABOUT_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToAboutAction"
            const val ACTION_HOME_GO_TO_PROPERTY_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToPropertyAction"
            const val ACTION_HOME_GO_TO_USER_SETTING_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToUserSettingAction"
            const val ACTION_HOME_GO_TO_GUEST_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToGuestAction"
            const val ACTION_HOME_GO_TO_PAINT_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToPaintAction"
            const val ACTION_HOME_GO_TO_OUTSOURCED_ACTION: String = "com.magtonic.MagtonicWarehoouse.HomeGoToOutsourcedAction"
            //property
            const val ACTION_PROPERTY_FRAGMENT_REFRESH : String ="com.magtonic.MagtonicWarehoouse.PropertyFragmentRefresh"
            const val ACTION_PROPERTY_MORE_DETAIL_REFRESH : String = "com.magtonic.MagtonicWarehoouse.PropertyMoreDetailRefresh"
            const val ACTION_PROPERTY_NO_NOT_EXIST : String = "com.magtonic.MagtonicWarehoouse.PropertyNoNotExist"
            const val ACTION_PROPERTY_SCAN_BARCODE : String = "com.magtonic.MagtonicWarehoouse.PropertyScanBarcode"
            const val ACTION_PROPERTY_MODIFY_CHANGED : String = "com.magtonic.MagtonicWarehoouse.PropertyModifyChanged"
            const val ACTION_PROPERTY_MODIFY_NO_CHANGED : String = "com.magtonic.MagtonicWarehoouse.PropertyModifyNoChanged"
            const val ACTION_PROPERTY_BACK_TO_LIST : String = "com.magtonic.MagtonicWarehoouse.PropertyBackToList"
            //const val ACTION_PROPERTY_SEEK_BAR_SELECT_PAGE_ACTION: String = "com.magtonic.MagtonicWarehoouse.PropertySeekbarSelectPageAction"
            //const val ACTION_PROPERTY_SEEK_BAR_SHOW_ACTION: String = "com.magtonic.MagtonicWarehoouse.PropertySeekbarShowAction"
            //const val ACTION_PROPERTY_SEEK_BAR_HIDE_ACTION: String = "com.magtonic.MagtonicWarehoouse.PropertySeekbarHideAction"
            //user setting
            const val ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_ON : String ="com.magtonic.MagtonicWarehoouse.SettingReceiptAutoConfirmUploadedOn"
            const val ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_OFF : String ="com.magtonic.MagtonicWarehoouse.SettingReceiptAutoConfirmUploadedOff"
            const val ACTION_SETTING_LOG_ENABLE_ON : String ="com.magtonic.MagtonicWarehoouse.SettingLogEnableOn"
            const val ACTION_SETTING_LOG_ENABLE_OFF : String ="com.magtonic.MagtonicWarehoouse.SettingLogEnableOff"
            //guest
            const val ACTION_GUEST_SCAN_BARCODE : String = "com.magtonic.MagtonicWarehoouse.GuestScanBarcode"
            const val ACTION_GUEST_GET_CURRENT_PLANT_GUEST_LIST : String = "com.magtonic.MagtonicWarehoouse.GuestGetCurrentPlantGuestList"
            const val ACTION_GUEST_GET_CURRENT_PLANT_GUEST_SUCCESS : String = "com.magtonic.MagtonicWarehoouse.GuestGetCurrentPlantGuestSuccess"
            const val ACTION_GUEST_GET_CURRENT_PLANT_GUEST_FAILED : String = "com.magtonic.MagtonicWarehoouse.GuestGetCurrentPlantGuestFailed"
            const val ACTION_GUEST_LIST_CLEAR : String ="com.magtonic.MagtonicWarehoouse.GuestListClear"
            const val ACTION_GUEST_FRAGMENT_REFRESH : String ="com.magtonic.MagtonicWarehoouse.GuestFragmentRefresh"
            const val ACTION_GUEST_IN_OR_LEAVE_ACTION : String ="com.magtonic.MagtonicWarehoouse.GuestInOrLeaveAction"
            const val ACTION_GUEST_IN_OR_LEAVE_FAILED : String ="com.magtonic.MagtonicWarehoouse.GuestInOrLeaveFailed"
            const val ACTION_GUEST_IN_OR_LEAVE_SUCCESS : String ="com.magtonic.MagtonicWarehoouse.GuestInOrLeaveSuccess"
            const val ACTION_GUEST_SHOW_LEAVE_ACTION : String ="com.magtonic.MagtonicWarehoouse.GuestShowLeaveAction"
            const val ACTION_GUEST_SEARCH_GUEST_LIST_ACTION : String = "com.magtonic.MagtonicWarehoouse.GuestSearchGuestList"
            const val ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION : String ="com.magtonic.MagtonicWarehoouse.GuestSearchGuestNext"
            const val ACTION_GUEST_SEARCH_GUEST_COMPLETE : String ="com.magtonic.MagtonicWarehoouse.GuestSearchGuestComplete"
            //outsourced process
            const val ACTION_OUTSOURCED_PROCESS_SCAN_BARCODE : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessScanBarcode"
            const val ACTION_OUTSOURCED_PROCESS_FRAGMENT_REFRESH : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessFragmentRefresh"
            const val ACTION_OUTSOURCED_PROCESS_FRAGMENT_DETAIL_REFRESH : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessFragmentDetailRefresh"
            const val ACTION_OUTSOURCED_PROCESS_FRAGMENT_MORE_DETAIL_REFRESH : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessFragmentMoreDetailRefresh"
            const val ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessBackToSupplierList"
            const val ACTION_OUTSOURCED_PROCESS_BACK_TO_DETAIL_LIST : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessBackToDetailList"

            const val ACTION_OUTSOURCED_PROCESS_SHOW_FAB_BACK : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessShowFabBack"
            const val ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessHideFabBack"

            const val ACTION_OUTSOURCED_PROCESS_SHOW_SIGN_DIALOG_ACTION : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessShowSignDialogAction"

            const val ACTION_OUTSOURCED_PROCESS_MODIFY_CHANGED : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessModifyChanged"
            const val ACTION_OUTSOURCED_PROCESS_NO_CHANGED : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessModifyNoChanged"
            const val ACTION_OUTSOURCED_PROCESS_MOVE_TO_POSITION : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessMoveToPosition"
            const val ACTION_OUTSOURCED_PROCESS_GET_DETAIL_BY_SEND_ORDER : String = "com.magtonic.MagtonicWarehoouse.OutsourcedProcessGetDetailBySendOrder"

            const val ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_TIMEOUT : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignFTPConnectTimeout"
            const val ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_UNKNOWN_HOST : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignFTPConnectUnknownHost"
            const val ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_FAILED : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignFTPConnectFailed"
            const val ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_SUCCESS : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignFTPConnectSuccess"
            const val ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_FAILED : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignFTPUploadFailed"
            const val ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_SUCCESS : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignFTPUploadSuccess"
            const val ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_COMPLETE : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignFTPUploadComplete"

            const val ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_ACTION : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignUploadAction"
            const val ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_FAILED : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignUploadFailed"
            const val ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_SUCCESS : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessSignUploadSuccess"

            const val ACTION_OUTSOURCED_PROCESS_SHOW_SUPPLIER_DIALOG : String ="com.magtonic.MagtonicWarehoouse.OutsourcedProcessShowSupplierDialog"

            //Issuance Lookup
            const val ACTION_ISSUANCE_LOOKUP_SCAN_BARCODE : String = "com.magtonic.MagtonicWarehoouse.IssuanceLookupScanBarcode"
            const val ACTION_ISSUANCE_LOOKUP_FRAGMENT_REFRESH : String = "com.magtonic.MagtonicWarehoouse.IssuanceLookupFragmentRefresh"
            const val ACTION_ISSUANCE_LOOKUP_FRAGMENT_DETAIL_REFRESH : String = "com.magtonic.MagtonicWarehoouse.IssuanceLookupFragmentDetailRefresh"
            const val ACTION_ISSUANCE_LOOKUP_BACK_TO_LIST : String = "com.magtonic.MagtonicWarehoouse.IssuanceLookupBackToList"
            const val ACTION_ISSUANCE_LOOKUP_SHOW_FAB_BACK : String ="com.magtonic.MagtonicWarehoouse.IssuanceLookupShowFabBack"
            const val ACTION_ISSUANCE_LOOKUP_HIDE_FAB_BACK : String ="com.magtonic.MagtonicWarehoouse.IssuanceLookupHideFabBack"
        }

    }
}