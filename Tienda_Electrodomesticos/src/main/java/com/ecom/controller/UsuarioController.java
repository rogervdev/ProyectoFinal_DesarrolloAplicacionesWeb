package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Cart;
import com.ecom.model.Categoria;
import com.ecom.model.Usuario;
import com.ecom.service.CartService;
import com.ecom.service.CategoriaService;
import com.ecom.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String home() {
        return "usuario/inicio";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            Usuario usuario = usuarioService.getUserByEmail(email);
            m.addAttribute("usuario", usuario);
            Integer countCart = cartService.getCountCart(usuario.getId());
            m.addAttribute("countCart", countCart);
           
        }
        List<Categoria> allActiveCategoria = categoriaService.getAllActiveCategoria();
        m.addAttribute("categorias", allActiveCategoria);
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid,
            @RequestParam Integer uid,
            HttpSession session) {
        Cart saveCart = cartService.saveCart(pid, uid);

        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Falla al agregar el producto al carrito");
        } else {
            session.setAttribute("successMsg", "✅ Producto agregado al carrito con éxito");
        }

        // Asegúrate de que la ruta coincida con tu método de detalle
        return "redirect:/ver_productos/" + pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m) {
        Usuario usuario = getLoggedInUsuarioDetalles(p);
        List<Cart> carts = cartService.getCartsByUser(usuario.getId());
        m.addAttribute("carts", carts);
        Double precioTotalDeOrdenes = carts.get(carts.size()-1).getTotalPrecioOrdenes();
        m.addAttribute("precioTotalOrdenes", precioTotalDeOrdenes);

        return "/usuario/cart";
    }

    private Usuario getLoggedInUsuarioDetalles(Principal p) {
        String email = p.getName();
        Usuario usuario = usuarioService.getUserByEmail(email);
        return usuario;
    }

}
