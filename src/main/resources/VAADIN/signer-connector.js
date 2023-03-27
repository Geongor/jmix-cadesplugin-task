let selectedCert;
let connector;

screen_ui_js_component_signer = function () {
    connector = this;
    connector.loadCerts = loadCerts;
    connector.saveSelected = saveSelected;
    connector.signFile = signFile;

    function loadCerts() {
        let store;
        try {
            cadesplugin.async_spawn(function* (args) {
                store = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
                yield store.Open();
                let certs = yield store.Certificates;
                let certCnt = yield certs.Count;
                let arr = [];
                let thumbArr = [];
                let cert;
                for (let i = 1; i <= certCnt; i++) {
                    cert = yield certs.Item(i);
                    let thumbprint = yield cert.Thumbprint;
                    thumbArr.push(thumbprint);
                    let name = yield cert.SubjectName;
                    arr.push(name);
                }
                connector.onCertsLoad(arr, thumbArr);
            });
        } catch (e) {
            console.log(e);
        }
    }

    function saveSelected(selected) {
        selectedCert = selected;
    }

    function signFile(toSign) {
        try {
            cadesplugin.async_spawn(function* (args) {
                let store = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
                yield store.Open();
                let certs = yield store.Certificates;
                let foundCert = yield certs.Find(0, selectedCert);
                let cert = yield foundCert.Item(1);
                let oSigner = yield cadesplugin.CreateObjectAsync("CAdESCOM.CPSigner");
                if (oSigner) {
                    yield oSigner.propset_Certificate(cert);
                } else {
                    let errormes = "Failed to create CAdESCOM.CPSigner";
                    throw errormes;
                }
                let oSignedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.CadesSignedData");
                try {
                    yield oSignedData.propset_Content(toSign);
                    let Signature = yield oSignedData.SignCades(oSigner, 1);
                    console.log(Signature);
                    connector.onSignCreated(Signature);
                } catch (err) {
                    let errormes = "Failed to sign file: " + cadesplugin.getLastError(err);
                    throw errormes;
                }

            });
        } catch (e) {
            console.log(e);
        }
    }
}
