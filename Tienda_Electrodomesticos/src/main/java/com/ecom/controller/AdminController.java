package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Categoria;
import com.ecom.model.Producto;
import com.ecom.model.Usuario;
import com.ecom.service.CartService;
import com.ecom.service.CategoriaService;
import com.ecom.service.ProductoService;
import com.ecom.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private ProductoService productoService;

	@Autowired
	private UsuarioService usuarioService;

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
		m.addAttribute("categorias", allActiveCategoria);
	}

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/agregar_producto")
	public String agregarProducto(Model m) {
		List<Categoria> categorias = categoriaService.getAllCategoria();
		m.addAttribute("categorias", categorias);
		return "admin/agregar_producto";
	}

	@GetMapping("/categoria")
	public String categoria(Model m) {
		m.addAttribute("categorias", categoriaService.getAllCategoria());
		return "admin/categoria";
	}

	@PostMapping("/guardar_categoria")
	public String guardarCategoria(@ModelAttribute Categoria categoria, @RequestParam MultipartFile file,
			HttpSession session) {

		String imagenNombre = file != null ? file.getOriginalFilename() : "default.jpg";
		categoria.setImagenNombre(imagenNombre);

		Boolean existeCategoria = categoriaService.existeCategoria(categoria.getNombre());
		if (existeCategoria) {
			session.setAttribute("errorMsg", "La categoría ya existe");

		} else {
			Categoria guardarCategoria = categoriaService.guardarCategoria(categoria);
			if (ObjectUtils.isEmpty(guardarCategoria)) {
				session.setAttribute("errorMsg", "No se pudo guardar la categoría | Error del Servidor");
			} else {
				try {
					File guardarFile = new File("src/main/resources/static/img/category_img");

					if (file != null && !file.isEmpty()) {
						Path path = Paths.get(
								guardarFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

						System.out.println(path);
						Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					}

					session.setAttribute("successMsg", "Categoría guardada correctamente");

				} catch (IOException e) {
					e.printStackTrace();
					session.setAttribute("errorMsg", "Error al guardar la imagen: " + e.getMessage());
				}
			}

		}
		return "redirect:/admin/categoria";
	}

	@GetMapping("/eliminar_categoria/{id}")
	public String eliminarCategoria(@PathVariable int id, HttpSession session) {
		{

			Boolean eliminarCategoria = categoriaService.eliminarCategoria(id);
			if (eliminarCategoria) {
				session.setAttribute("successMsg", "Categoría eliminada correctamente");
			} else {
				session.setAttribute("errorMsg", "No se pudo eliminar la categoría | Error del Servidor");
			}

			return "redirect:/admin/categoria";
		}

	}

	@GetMapping("/editar_categoria/{id}")
	public String editarCategoria(@PathVariable int id, Model m) {
		m.addAttribute("categoria", categoriaService.obtenerCategoriaPorId(id));
		return "admin/editar_categoria";
	}

	@PostMapping("/actualizar_categoria")
	public String actualizarCategoria(@ModelAttribute Categoria categoria,
			@RequestParam MultipartFile file,
			HttpSession session) {

		Categoria viejaCategoria = categoriaService.obtenerCategoriaPorId(categoria.getId());
		if (viejaCategoria == null) {
			session.setAttribute("errorMsg", "Categoría no encontrada");
			return "redirect:/admin/categoria";
		}

		String imagenNombre = file.isEmpty() ? viejaCategoria.getImagenNombre() : file.getOriginalFilename();

		// Actualizamos los datos
		viejaCategoria.setNombre(categoria.getNombre());
		viejaCategoria.setIsActive(categoria.getIsActive());
		viejaCategoria.setImagenNombre(imagenNombre);

		Categoria actualizarCategoria = categoriaService.guardarCategoria(viejaCategoria);

		if (!ObjectUtils.isEmpty(actualizarCategoria)) {

			if (!file.isEmpty()) {
				try {
					File guardarFile = new File("src/main/resources/static/img/category_img");
					Path path = Paths.get(guardarFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

					// Forzamos que el objeto categoria tenga la nueva imagen
					actualizarCategoria.setImagenNombre(file.getOriginalFilename());

				} catch (IOException e) {
					e.printStackTrace();
					session.setAttribute("errorMsg", "Error al guardar la imagen");
				}
			}

			session.setAttribute("successMsg", "Categoría actualizada correctamente");

		} else {
			session.setAttribute("errorMsg", "No se pudo actualizar la categoría | Error del Servidor");
		}

		// Redireccionamos de nuevo a la página de edición
		return "redirect:/admin/editar_categoria/" + categoria.getId();
	}

	@PostMapping("/guardar_producto")
	public String guardarProducto(
			@ModelAttribute Producto producto,
			@RequestParam("file") MultipartFile image,
			HttpSession session) {

		try {
			// 🖼️ Nombre del archivo de imagen
			String imagenNombre = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
			producto.setImagen(imagenNombre);
			producto.setDescuento(0);
			producto.setPrecioConDescuento(producto.getPrecio());

			// 💾 Guardar producto en la base de datos
			Producto guardarProducto = productoService.guardarProducto(producto);

			if (!ObjectUtils.isEmpty(guardarProducto)) {
				// 📁 Carpetas de guardado
				File carpetaSrc = new File("src/main/resources/static/img/product_img");
				File carpetaTarget = new File("target/classes/static/img/product_img");

				if (!carpetaSrc.exists())
					carpetaSrc.mkdirs();
				if (!carpetaTarget.exists())
					carpetaTarget.mkdirs();

				if (!image.isEmpty()) {
					// 📂 Guardar en ambas ubicaciones
					Path pathSrc = Paths.get(carpetaSrc.getAbsolutePath(), image.getOriginalFilename());
					Path pathTarget = Paths.get(carpetaTarget.getAbsolutePath(), image.getOriginalFilename());

					// Sobrescribir si ya existe
					Files.copy(image.getInputStream(), pathSrc, StandardCopyOption.REPLACE_EXISTING);
					Files.copy(image.getInputStream(), pathTarget, StandardCopyOption.REPLACE_EXISTING);
				}

				session.setAttribute("successMsg", "✅ Producto guardado correctamente");
			} else {
				session.setAttribute("errorMsg", "❌ No se pudo guardar el producto | Error del Servidor");
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("errorMsg", "⚠️ Error al guardar el producto: " + e.getMessage());
		}

		return "redirect:/admin/agregar_producto";
	}

	@GetMapping("/productos")
	public String verProductos(Model m) {
		m.addAttribute("productos", productoService.getAllProductos());
		return "admin/productos";
	}

	@GetMapping("/eliminar_producto/{id}")
	public String verProductos(@PathVariable int id, HttpSession session) {
		Boolean eliminarProducto = productoService.eliminarProducto(id);
		if (eliminarProducto) {
			session.setAttribute("successMsg", "Producto eliminado correctamente");
		} else {
			session.setAttribute("errorMsg", "No se pudo eliminar el producto | Error del Servidor");
		}
		return "redirect:/admin/productos";
	}

	@GetMapping("/editar_producto/{id}")
	public String editarProducto(@PathVariable int id, Model m) {
		m.addAttribute("producto", productoService.getProductoById(id));
		m.addAttribute("categorias", categoriaService.getAllCategoria());

		return "admin/editar_producto";
	}

	@PostMapping("/actualizar_producto")
	public String actualizarProducto(@ModelAttribute Producto producto, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		if (producto.getDescuento() < 0 || producto.getDescuento() > 100) {
			session.setAttribute("errorMsg", "El descuento debe estar entre 0 y 100");
		} else {
			Producto actualizarProducto = productoService.actualizarProducto(producto, image);
			if (!ObjectUtils.isEmpty(actualizarProducto)) {
				session.setAttribute("successMsg", "Producto actualizado correctamente");
			} else {
				session.setAttribute("errorMsg", "No se pudo actualizar el producto | Error del Servidor");
			}
		}
		return "redirect:/admin/editar_producto/" + producto.getId();
	}

	@GetMapping("/usuarios")
	public String getAllUsuarios(Model m) {

		List<Usuario> usuarios = usuarioService.getUsuarios("ROLE_USER");
		m.addAttribute("usuarios", usuarios);
		return "admin/usuarios";
	}

	@GetMapping("/usuarioActualizado")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
		Boolean f = usuarioService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "La cuenta ha sido Actualizada");
		} else {
			session.setAttribute("errorMsg", "Error del Servidor");
		}
		return "redirect:/admin/usuarios";
	}
}
