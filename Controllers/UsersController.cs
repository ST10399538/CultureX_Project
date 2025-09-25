using CultureXAPI.Data;
using CultureXAPI.DTOs;
using CultureXAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using System.Security.Claims;

namespace CultureXAPI.Controllers
{

    [ApiController]
    [Route("api/[controller]")]
    [Authorize]
    public class UsersController : ControllerBase
    {

        private readonly CultureXDbContext _context;

        public UsersController(CultureXDbContext context)
        {
            _context = context;
        }

        private Guid GetUserId()
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            return Guid.Parse(userIdClaim);
        }

        [HttpGet("profile")]
        public async Task<ActionResult<UserProfileDTO>> GetProfile()
        {
            var userId = GetUserId();

            var user = await _context.Users.FindAsync(userId);

            if (user == null)
            {
                return NotFound("User not found");
            }

            var userProfileDto = new UserProfileDTO
            {
                Id = user.Id,
                Email = user.Email,
                DisplayName = user.DisplayName,
                ProfilePictureUrl = user.ProfilePictureUrl,
                PreferredLanguage = user.PreferredLanguage,
                BiometricEnabled = user.BiometricEnabled,
                NotificationPreferences = JsonConvert.DeserializeObject(user.NotificationPreferences ?? "{}")
            };

            return Ok(userProfileDto);
        }

        [HttpPut("profile")]
        public async Task<ActionResult<UserProfileDTO>> UpdateProfile(UpdateUserProfileDTO updateProfileDto)
        {
            var userId = GetUserId();

            var user = await _context.Users.FindAsync(userId);

            if (user == null)
            {
                return NotFound("User not found");
            }

            user.DisplayName = updateProfileDto.DisplayName;
            user.ProfilePictureUrl = updateProfileDto.ProfilePictureUrl;
            user.PreferredLanguage = updateProfileDto.PreferredLanguage;
            user.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            var userProfileDto = new UserProfileDTO
            {
                Id = user.Id,
                Email = user.Email,
                DisplayName = user.DisplayName,
                ProfilePictureUrl = user.ProfilePictureUrl,
                PreferredLanguage = user.PreferredLanguage,
                BiometricEnabled = user.BiometricEnabled,
                NotificationPreferences = JsonConvert.DeserializeObject(user.NotificationPreferences ?? "{}")
            };

            return Ok(userProfileDto);
        }

        [HttpGet("settings")]
        public async Task<ActionResult<UserSettingsDTO>> GetSettings()
        {
            var userId = GetUserId();

            var user = await _context.Users.FindAsync(userId);

            if (user == null)
            {
                return NotFound("User not found");
            }

            var settingsDto = new UserSettingsDTO
            {
                PreferredLanguage = user.PreferredLanguage,
                BiometricEnabled = user.BiometricEnabled,
                NotificationPreferences = JsonConvert.DeserializeObject(user.NotificationPreferences ?? "{}")
            };

            return Ok(settingsDto);
        }

        [HttpPut("settings")]
        public async Task<ActionResult<UserSettingsDTO>> UpdateSettings(UpdateUserSettingsDTO updateSettingsDto)
        {
            var userId = GetUserId();

            var user = await _context.Users.FindAsync(userId);

            if (user == null)
            {
                return NotFound("User not found");
            }

            user.PreferredLanguage = updateSettingsDto.PreferredLanguage;
            user.BiometricEnabled = updateSettingsDto.BiometricEnabled;
            user.NotificationPreferences = JsonConvert.SerializeObject(updateSettingsDto.NotificationPreferences);
            user.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            var settingsDto = new UserSettingsDTO
            {
                PreferredLanguage = user.PreferredLanguage,
                BiometricEnabled = user.BiometricEnabled,
                NotificationPreferences = JsonConvert.DeserializeObject(user.NotificationPreferences ?? "{}")
            };

            return Ok(settingsDto);
        }

        [HttpGet("favorites")]
        public async Task<ActionResult<IEnumerable<FavoriteDTO>>> GetFavorites()
        {
            var userId = GetUserId();

            var favorites = await _context.UserFavorites
                .Include(uf => uf.Country)
                .Include(uf => uf.Content)
                .ThenInclude(cc => cc.Category)
                .Where(uf => uf.UserId == userId)
                .Select(uf => new FavoriteDTO
                {
                    Id = uf.Id,
                    CountryId = uf.CountryId,
                    ContentId = uf.ContentId,
                    CountryName = uf.Country != null ? uf.Country.Name : null,
                    ContentTitle = uf.Content != null ? uf.Content.Title : null,
                    CategoryName = uf.Content != null && uf.Content.Category != null ? uf.Content.Category.Name : null,
                    CreatedAt = uf.CreatedAt
                })
                .OrderByDescending(f => f.CreatedAt)
                .ToListAsync();

            return Ok(favorites);
        }

        [HttpPost("favorites")]
        public async Task<ActionResult> AddFavorite(AddFavoriteDTO addFavoriteDto)
        {
            var userId = GetUserId();

            // Check if already favorited
            var existingFavorite = await _context.UserFavorites
                .FirstOrDefaultAsync(uf => uf.UserId == userId &&
                                          uf.CountryId == addFavoriteDto.CountryId &&
                                          uf.ContentId == addFavoriteDto.ContentId);

            if (existingFavorite != null)
            {
                return BadRequest("Already added to favorites");
            }

            var favorite = new UserFavorite
            {
                UserId = userId,
                CountryId = addFavoriteDto.CountryId,
                ContentId = addFavoriteDto.ContentId
            };

            _context.UserFavorites.Add(favorite);
            await _context.SaveChangesAsync();

            return Ok("Added to favorites");
        }

        [HttpDelete("favorites/{id}")]
        public async Task<ActionResult> RemoveFavorite(Guid id)
        {
            var userId = GetUserId();

            var favorite = await _context.UserFavorites
                .FirstOrDefaultAsync(uf => uf.Id == id && uf.UserId == userId);

            if (favorite == null)
            {
                return NotFound("Favorite not found");
            }

            _context.UserFavorites.Remove(favorite);
            await _context.SaveChangesAsync();

            return NoContent();
        }

    }
}
