package com.ecom.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Categoria;
import com.ecom.model.Producto;
import com.ecom.model.Usuario;
import com.ecom.service.CartService;
import com.ecom.service.CategoriaService;
import com.ecom.service.ProductoService;
import com.ecom.service.UsuarioService;
import com.ecom.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private ProductoService productoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private CartService cartService;

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
		m.addAttribute("categoria", allActiveCategoria);

		// 🔹 Agregar últimos productos
		List<Producto> productos = productoService.getAllActiveProducto(""); // "" = todos
		m.addAttribute("productos", productos);
	}

	@GetMapping("/")
	public String index(Model m) {
		List<Categoria> categorias = categoriaService.getAllActiveCategoria();
		m.addAttribute("categorias", categorias);
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/registro")
	public String register() {
		return "registro";
	}

	@GetMapping("/productos")
	public String Productos(Model m, @RequestParam(defaultValue = "") String categoria) {
		List<Categoria> categorias = categoriaService.getAllActiveCategoria();
		List<Producto> productos = productoService.getAllActiveProducto(categoria);
		m.addAttribute("categorias", categorias);
		m.addAttribute("productos", productos);
		m.addAttribute("paramValue", categoria);
		return "producto";
	}

	@GetMapping("/ver_productos/{id}")
	public String verProducto(@PathVariable Integer id, Model model) {
		Producto producto = productoService.getProductoById(id);
		model.addAttribute("producto", producto);
		return "ver_productos";
	}

	@PostMapping("/saveUsuario")
	public String saveUsuario(@ModelAttribute Usuario usuario, @RequestParam("img") MultipartFile file,
			HttpSession session) throws IOException {

		// 🔹 1. Verificar si el correo ya existe
		if (usuarioService.existsByEmail(usuario.getEmail())) {
			session.setAttribute("errorMsg", "El correo ya está registrado. Intente con otro.");
			return "redirect:/registro";
		}

		// 🔹 2. Preparar nombre de imagen
		String imagenNombre = file.isEmpty() ? "default.jpg" : "profile_img_" + file.getOriginalFilename();
		usuario.setProfileImage(imagenNombre);

		// 🔹 3. Guardar usuario en la BD
		Usuario saveUsuario = usuarioService.saveUsuario(usuario);

		if (!ObjectUtils.isEmpty(saveUsuario)) {
			// 🔹 4. Guardar imagen en carpeta si no está vacía
			if (!file.isEmpty()) {
				File saveDir = new ClassPathResource("static/img").getFile();

				// crear la carpeta si no existe
				if (!saveDir.exists()) {
					saveDir.mkdirs();
				}

				Path path = Paths.get(saveDir.getAbsolutePath() + File.separator + imagenNombre);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("successMsg", "¡Usuario registrado correctamente!");
		} else {
			session.setAttribute("errorMsg", "¡Ocurrió un error al registrar el usuario!");
		}

		return "redirect:/registro";
	}

	// Contraseña olvidada
	@GetMapping("/forgot-password")
	public String ShowForgotPassword() {
		return "forgot_password";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		Usuario userByEmail = usuarioService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "Email inválido");
		} else {
			String resetToken = UUID.randomUUID().toString();
			usuarioService.updateUserResetToken(email, resetToken);

			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			// 👇 ya no esperamos un retorno, solo lo ejecutamos
			commonUtil.sendMail(url, email);

			// ✅ Mostramos mensaje de éxito inmediatamente
			session.setAttribute("successMsg", "Revisa tu correo, el link ha sido enviado");
		}

		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {
		Usuario userByToken = usuarioService.getUserByToken(token);
		if (userByToken == null) {
			m.addAttribute("msg", "Tu link ha expirado");
			return "message";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token,
			@RequestParam String password,
			@RequestParam String confirmPassword,
			HttpSession session, Model m) {
		if (!password.equals(confirmPassword)) {
			m.addAttribute("errorMsg", "Las contraseñas no coinciden");
			m.addAttribute("token", token);
			return "reset_password";
		}

		Usuario userByToken = usuarioService.getUserByToken(token);
		if (userByToken == null) {
			m.addAttribute("errorMsg", "Tu link ha expirado");
			return "message";
		}
		userByToken.setPassword(passwordEncoder.encode(password));
		userByToken.setResetToken(null);
		usuarioService.updateUser(userByToken);

		session.setAttribute("successMsg", "Contraseña cambiada");
		m.addAttribute("msg", "Contraseña cambiada exitosamente");
		return "message";
	}

	@GetMapping("buscar")
	public String buscarProducto(@RequestParam String ch, Model m) {
		List<Producto> buscarProductos = productoService.buscarProducto(ch);
		m.addAttribute("productos", buscarProductos);
		return "producto";
	}
}
