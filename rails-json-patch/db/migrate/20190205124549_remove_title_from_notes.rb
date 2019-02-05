class RemoveTitleFromNotes < ActiveRecord::Migration[5.2]
  def change
    remove_column :notes, :title, :text
  end
end
