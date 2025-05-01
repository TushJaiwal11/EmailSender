package com.tj.email.service.impl;

import com.tj.email.exception.UserException;
import com.tj.email.model.PDF;
import com.tj.email.model.User;
import com.tj.email.model.domain.UserRole;
import com.tj.email.model.dto.PDFDTO;
import com.tj.email.model.dto.UserDto;
import com.tj.email.repository.PDFRepository;
import com.tj.email.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PDFServiceImpl implements PDFService {

    @Autowired
    private PDFRepository pdfRepository;

    @Override
    public PDF savePdf(UserDto user, PDFDTO pdfdto) throws UserException, IOException {

        if(!user.getRole().equals(UserRole.ROLE_ADMIN)){
            throw new UserException("Only admin can upload the pdf file");
        }

        byte[] pdf = Base64.getDecoder().decode(pdfdto.getPdfData());
        PDF p = new PDF();

        p.setTitle(pdfdto.getTitle());
        p.setPdfData(pdf);
        p.setCreateAt(LocalDateTime.now());

        return pdfRepository.save(p);
    }

    @Override
    public List<PDFDTO> getAllPdf() {

        List<PDF> pdfs = pdfRepository.findAll(Sort.by(Sort.Direction.DESC, "createAt"));


        return pdfs.stream().map(pdf -> {
            PDFDTO dto = new PDFDTO();
            dto.setTitle(pdf.getTitle());
            dto.setCreateAt(pdf.getCreateAt());
            dto.setPdfData(Base64.getEncoder().encodeToString(pdf.getPdfData()));
            return dto;
        }).collect(Collectors.toList());
    }
}
