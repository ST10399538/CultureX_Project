using CultureXAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace CultureXAPI.Data
{
    public class CultureXDbContext : DbContext
    {

        public CultureXDbContext(DbContextOptions<CultureXDbContext> options) : base(options)
        {
        }

        public DbSet<User> Users { get; set; }
        public DbSet<Country> Countries { get; set; }
        public DbSet<CulturalCategory> CulturalCategories { get; set; }
        public DbSet<CulturalContent> CulturalContents { get; set; }
        public DbSet<UserItinerary> UserItineraries { get; set; }
        public DbSet<UserFavorite> UserFavorites { get; set; }
        public DbSet<UserSession> UserSessions { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Configure relationships and constraints
            modelBuilder.Entity<User>()
                .HasIndex(u => u.Email)
                .IsUnique();

            modelBuilder.Entity<CulturalContent>()
                .HasOne(cc => cc.Country)
                .WithMany(c => c.CulturalContents)
                .HasForeignKey(cc => cc.CountryId);

            modelBuilder.Entity<CulturalContent>()
                .HasOne(cc => cc.Category)
                .WithMany(cat => cat.Contents)
                .HasForeignKey(cc => cc.CategoryId);

            modelBuilder.Entity<UserItinerary>()
                .HasOne(ui => ui.User)
                .WithMany(u => u.Itineraries)
                .HasForeignKey(ui => ui.UserId);

            modelBuilder.Entity<UserFavorite>()
                .HasOne(uf => uf.User)
                .WithMany(u => u.Favorites)
                .HasForeignKey(uf => uf.UserId);
        }

    }
}
