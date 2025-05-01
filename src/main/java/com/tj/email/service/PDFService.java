package com.tj.email.service;

import com.tj.email.exception.UserException;
import com.tj.email.model.PDF;
import com.tj.email.model.User;
import com.tj.email.model.dto.PDFDTO;
import com.tj.email.model.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PDFService {


    public PDF savePdf(UserDto user, PDFDTO pdfdto) throws UserException, IOException;

    public List<PDFDTO> getAllPdf();
}
