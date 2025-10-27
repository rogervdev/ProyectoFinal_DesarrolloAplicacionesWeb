package com.ecom.service.impl;

import com.ecom.service.CommonService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CommonServiceImpl implements CommonService {

    @Override
    public void removeSessionMessage() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            HttpServletRequest request = attr.getRequest();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("successMsg");
                session.removeAttribute("errorMsg");
            }
        }
    }

}
