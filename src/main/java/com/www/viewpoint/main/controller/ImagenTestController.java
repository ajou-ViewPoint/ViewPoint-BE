package com.www.viewpoint.main.controller;

import com.www.viewpoint.main.service.ImagenClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/test")
//public class ImagenTestController {
//
//    private final ImagenClient imagenClient;
//
//    /**
//     * 예: GET /api/test/imagen?prompt=Robot%20holding%20a%20red%20skateboard&count=1
//     */
//    @GetMapping("/imagen")
//    public ResponseEntity<?> testImagen(
//            @RequestParam(defaultValue = "Robot holding a red skateboard")
//            String prompt,
//            @RequestParam(name = "count", defaultValue = "1")
//            int sampleCount
//    ) {
//        String result = imagenClient.debugRawPredict(prompt, sampleCount);
//        return ResponseEntity.ok(result);
//    }
//}