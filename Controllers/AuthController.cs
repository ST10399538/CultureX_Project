using CultureXAPI.Data;
using CultureXAPI.DTOs;
using CultureXAPI.Models;
using CultureXAPI.Services;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Microsoft.EntityFrameworkCore;
using BCrypt.Net;

namespace CultureXAPI.Controllers
{

    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {

        private readonly CultureXDbContext _context;
        private readonly IJwtService _jwtService;

        public AuthController(CultureXDbContext context, IJwtService jwtService)
        {
            _context = context;
            _jwtService = jwtService;
        }

        [HttpPost("register")]
        public async Task<ActionResult<AuthResponseDTO>> Register(RegisterDTO registerDto)
        {
            // Check if user already exists
            if (await _context.Users.AnyAsync(u => u.Email == registerDto.Email))
            {
                return BadRequest("User with this email already exists");
            }

            // Create new user
            var user = new User
            {
                Email = registerDto.Email,
                DisplayName = registerDto.DisplayName,
                PreferredLanguage = registerDto.PreferredLanguage,
                NotificationPreferences = JsonConvert.SerializeObject(new
                {
                    PushEnabled = true,
                    EmailEnabled = true,
                    TravelReminders = true
                })
            };

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            // Generate tokens
            var accessToken = _jwtService.GenerateAccessToken(user);
            var refreshToken = _jwtService.GenerateRefreshToken();

            // Save refresh token
            var userSession = new UserSession
            {
                UserId = user.Id,
                RefreshToken = refreshToken,
                ExpiresAt = DateTime.UtcNow.AddDays(30)
            };

            _context.UserSessions.Add(userSession);
            await _context.SaveChangesAsync();

            return Ok(new AuthResponseDTO
            {
                AccessToken = accessToken,
                RefreshToken = refreshToken,
                User = new UserProfileDTO
                {
                    Id = user.Id,
                    Email = user.Email,
                    DisplayName = user.DisplayName,
                    ProfilePictureUrl = user.ProfilePictureUrl,
                    PreferredLanguage = user.PreferredLanguage,
                    BiometricEnabled = user.BiometricEnabled,
                    NotificationPreferences = JsonConvert.DeserializeObject(user.NotificationPreferences)
                }
            });
        }

        [HttpPost("login")]
        public async Task<ActionResult<AuthResponseDTO>> Login(LoginDTO loginDto)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Email == loginDto.Email);

            if (user == null)
            {
                return Unauthorized("Invalid email or password");
            }

            // Generate tokens
            var accessToken = _jwtService.GenerateAccessToken(user);
            var refreshToken = _jwtService.GenerateRefreshToken();

            // Clean up old sessions and create new one
            var oldSessions = await _context.UserSessions.Where(s => s.UserId == user.Id).ToListAsync();
            _context.UserSessions.RemoveRange(oldSessions);

            var userSession = new UserSession
            {
                UserId = user.Id,
                RefreshToken = refreshToken,
                ExpiresAt = DateTime.UtcNow.AddDays(30)
            };

            _context.UserSessions.Add(userSession);
            await _context.SaveChangesAsync();

            return Ok(new AuthResponseDTO
            {
                AccessToken = accessToken,
                RefreshToken = refreshToken,
                User = new UserProfileDTO
                {
                    Id = user.Id,
                    Email = user.Email,
                    DisplayName = user.DisplayName,
                    ProfilePictureUrl = user.ProfilePictureUrl,
                    PreferredLanguage = user.PreferredLanguage,
                    BiometricEnabled = user.BiometricEnabled,
                    NotificationPreferences = JsonConvert.DeserializeObject(user.NotificationPreferences ?? "{}")
                }
            });
        }
    }

}

