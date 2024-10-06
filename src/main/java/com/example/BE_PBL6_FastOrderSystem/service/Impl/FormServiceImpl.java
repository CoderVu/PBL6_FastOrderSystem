package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.ShipperRegistrationForm;
import com.example.BE_PBL6_FastOrderSystem.repository.ShipperRegistrationFormReponsitory;
import com.example.BE_PBL6_FastOrderSystem.request.FormRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.FormResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IFormService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements IFormService {
    private final ShipperRegistrationFormReponsitory shipperRegistrationFormReponsitory;
    @Override
    public ResponseEntity<APIRespone> getAllForms() {
        if (shipperRegistrationFormReponsitory.findAll().isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "No form found", ""), HttpStatus.BAD_REQUEST);
        }
        List<FormResponse> formResponses = shipperRegistrationFormReponsitory.findAll().stream()
                .map(form -> new FormResponse(form.getId(), form.getName(), form.getCitizenID(), form.getImageCitizenFront(), form.getImageCitizenBack(), form.getEmail(), form.getPhone(), form.getAddress(), form.getAge(), form.getVehicle(), form.getLicensePlate(), form.getDriverLicense()))
                .toList();
        return new ResponseEntity<>(new APIRespone(true, "Success", formResponses), HttpStatus.OK);
    }
    @Override
    public  ResponseEntity<APIRespone> addForm(FormRequest formRequest) {
        ShipperRegistrationForm shipperRegistrationForm = new ShipperRegistrationForm();
        shipperRegistrationForm.setName(formRequest.getName());
        shipperRegistrationForm.setCitizenID(formRequest.getCitizenID());
        try{
            InputStream imageCitizenFront = formRequest.getImageCitizenFront().getInputStream();
            String base64ImageCitizenFront = ImageGeneral.fileToBase64(imageCitizenFront);
            shipperRegistrationForm.setImageCitizenFront(base64ImageCitizenFront);
        } catch (IOException e) {
            return new ResponseEntity<>(new APIRespone(false, "Error when upload image", ""), HttpStatus.BAD_REQUEST);
        }
        try{
            InputStream imageCitizenBack = formRequest.getImageCitizenBack().getInputStream();
            String base64ImageCitizenBack = ImageGeneral.fileToBase64(imageCitizenBack);
            shipperRegistrationForm.setImageCitizenBack(base64ImageCitizenBack);
        } catch (IOException e) {
            return new ResponseEntity<>(new APIRespone(false, "Error when upload image", ""), HttpStatus.BAD_REQUEST);
        }
        shipperRegistrationForm.setEmail(formRequest.getEmail());
        shipperRegistrationForm.setPhone(formRequest.getPhone());
        shipperRegistrationForm.setAddress(formRequest.getAddress());
        shipperRegistrationForm.setAge(formRequest.getAge());
        shipperRegistrationForm.setVehicle(formRequest.getVehicle());
        shipperRegistrationForm.setLicensePlate(formRequest.getLicensePlate());
        shipperRegistrationForm.setDriverLicense(formRequest.getDriverLicense());
        shipperRegistrationFormReponsitory.save(shipperRegistrationForm);
        return new ResponseEntity<>(new APIRespone(true, "Success", ""), HttpStatus.OK);

    }
}
