package com.company.cadesplugin.screen.signpage;

import com.company.cadesplugin.screen.util.JsonUtils;
import elemental.json.JsonArray;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.FileStorageUploadField;
import io.jmix.ui.component.JavaScriptComponent;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.SelectList;
import io.jmix.ui.component.SingleFileUploadField;
import io.jmix.ui.component.SingleSelectList;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import io.jmix.ui.upload.TemporaryStorage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@UiController("SignPage")
@UiDescriptor("sign-page.xml")
public class SignPage extends Screen {
    @Autowired
    private JavaScriptComponent signer;
    @Autowired
    private SingleSelectList<String> certsList;
    @Autowired
    private FileStorageUploadField fileField;
    @Autowired
    private Label<String> fileLabel;
    @Autowired
    private TemporaryStorage temporaryStorage;

    @Subscribe
    public void onInit(InitEvent event) {
        signer.addFunction("onCertsLoad", javaScriptCallbackEvent -> {
            JsonArray certNames = javaScriptCallbackEvent.getArguments().getArray(0);
            JsonArray certThumbs = javaScriptCallbackEvent.getArguments().getArray(1);
            Map<String, String> options = JsonUtils.jsonArraysToMap(certNames, certThumbs);
            certsList.setOptionsMap(options);
        });
        signer.addFunction("onSignCreated", javaScriptCallbackEvent -> {
            System.out.println(javaScriptCallbackEvent.getArguments().getString(0));
        });
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        signer.callFunction("loadCerts");
    }

    @Subscribe("certsList")
    public void onCertsListDoubleClick(SelectList.DoubleClickEvent event) {
        signer.callFunction("saveSelected", event.getItem());
    }

    @Subscribe("loadCertsBtn")
    public void onTestBtnClick(Button.ClickEvent event) {
        signer.callFunction("loadCerts");
    }

    @Subscribe("signBtn")
    public void onSignBtnClick(Button.ClickEvent event) throws IOException {
        File file = temporaryStorage.getFile(fileField.getFileId());
        if (file != null) {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String baseEncodedFile = Base64.getEncoder().encodeToString(fileBytes);
            temporaryStorage.deleteFile(fileField.getFileId());
            signer.callFunction("signFile", baseEncodedFile);
        }
    }

    @Subscribe("fileField")
    public void onFileFieldFileUploadSucceed(SingleFileUploadField.FileUploadSucceedEvent event) {
        fileLabel.setValue(event.getFileName());
    }

}