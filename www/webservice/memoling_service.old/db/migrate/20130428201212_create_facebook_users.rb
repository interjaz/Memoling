class CreateFacebookUsers < ActiveRecord::Migration
  def change
    create_table :facebook_users do |t|
      t.string :name
      t.string :firstName
      t.string :lastName
      t.string :link
      t.string :username
      t.string :hometown
	  t.belongs_to :location
      t.string :gender
      t.string :timezone
      t.string :locale
      t.string :verified
      t.datetime :updatedTime

      t.timestamps
    end
  end
end
